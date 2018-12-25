package com.cadiducho.bot.modules.pole.cmds;

import com.cadiducho.bot.api.command.BotCommand;
import com.cadiducho.bot.api.command.CommandContext;
import com.cadiducho.bot.api.command.CommandInfo;
import com.cadiducho.bot.modules.pole.CachedGroup;
import com.cadiducho.bot.modules.pole.PoleCacheManager;
import com.cadiducho.bot.modules.pole.PoleCollection;
import com.cadiducho.bot.modules.pole.PoleModule;
import com.cadiducho.bot.modules.pole.util.PoleAntiCheat;
import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.vdurmont.emoji.EmojiManager;
import lombok.extern.java.Log;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log
@CommandInfo(module = PoleModule.class, aliases = "pole")
public class PoleCMD implements BotCommand {

    private final PoleModule module = (PoleModule) getModule();

    private final String emojiGift = EmojiManager.getForAlias("gift").getUnicode();
    private final String emojiTree = EmojiManager.getForAlias("christmas_tree").getUnicode();
    private final String emojiSnow = EmojiManager.getForAlias("snowflake").getUnicode();

    @SuppressWarnings({"ConstantConditions", "OptionalGetWithoutIsPresent"})
    @Override
    public void execute(final Chat chat, final User from, final CommandContext context, final Integer messageId, final Message replyingTo, Instant instant) throws TelegramException {
        if (!module.isChatSafe(getBot(), chat, from)) return;

        PoleAntiCheat antiCheat = module.getPoleAntiCheat();
        if (antiCheat.isFlooding(from.getId())) {
            getBot().sendMessage(chat.getId(), "Sistema antiflood activado, te quedas sin poles por listo crack");
            return;
        }
        
        LocalDateTime today = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        PoleCacheManager manager = module.getPoleCacheManager();
        Long groupId = Long.parseLong(chat.getId());
        String currentname = from.getFirstName();
        String base = "<i>" + DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault()).format(instant) + "</i>: " + currentname;

        // Obtener grupo del caché y/o cargar este
        if (!manager.isCached(groupId)) {
            manager.initializeGroupCache(groupId, chat.getTitle(), manager.getChatLastAdded(groupId));
        }
        CachedGroup cachedGroup = manager.getCachedGroup(groupId).get();

        // Si el bot ha sido añadido hoy, no se podrán hacer poles hasta el siguiente 00:00:00
        if (today.toLocalDate().isEqual(cachedGroup.getLastAdded())) {
            getBot().sendMessage(chat.getId(), "Lo siento, pero he sido añadido al grupo hoy.\nNo se podrán realizar poles hasta la siguiente medianoche");
            return;
        }

        cachedGroup.setTitle(chat.getTitle()); // actualizar titulo del grupo

        // Obtener lista de poles hechas hoy y gestionar la posición de el intento actual
        Optional<PoleCollection> poles = cachedGroup.getPolesOfADay(today.toLocalDate());
        if (!poles.isPresent()) { // si no hay lista, es el primer puesto
            PoleCollection polesHoy = PoleCollection.builder().first(from.getId()).build();
            cachedGroup.getPolesMap().put(today.toLocalDate(), polesHoy);
            save(manager, cachedGroup, today.toLocalDate(), polesHoy);
            saveToDatabase(manager, antiCheat, cachedGroup, polesHoy, 1);
            getBot().sendMessage(chat.getId(), base + " ha hecho la <b>pole</b> navideña" + emojiGift + "!!!", "html", null, false, messageId, null);
            log.info("Pole otorgado a " + from.getId() + " en " + chat.getId());
        } else if (!poles.get().contains(from.getId())) {
            if (!poles.get().getFirst().isPresent() && !poles.get().getSecond().isPresent() && !poles.get().getThird().isPresent()) { //fixbug a si el objeto PolleCollection existe en memoria pero realmente no se han realizado poles
                poles.get().setFirst(from.getId());
                save(manager, cachedGroup, today.toLocalDate(), poles.get());
                saveToDatabase(manager, antiCheat, cachedGroup, poles.get(), 1);
                getBot().sendMessage(chat.getId(), base + " ha hecho la <b>pole</b><i>*</i>!!!", "html", null, false, messageId, null);
                log.info("Pole (fixbug) otorgado a " + from.getId() + " en " + chat.getId());
            } else if (!poles.get().getSecond().isPresent()) { // si hay lista y el segundo no está presente, es plata
                poles.get().setSecond(from.getId());
                save(manager, cachedGroup, today.toLocalDate(), poles.get());
                saveToDatabase(manager, antiCheat, cachedGroup, poles.get(), 2);
                getBot().sendMessage(chat.getId(), base + " ha hecho la <b>subpole</b> navideña" + emojiTree + ", meh", "html", null, false, messageId, null);
                log.info("Plata otorgado a " + from.getId() + " en " + chat.getId());
            } else if (!poles.get().getThird().isPresent()) { // si hay lista y el tercero no está presente, es plata
                poles.get().setThird(from.getId());
                save(manager, cachedGroup, today.toLocalDate(), poles.get());
                saveToDatabase(manager, antiCheat, cachedGroup, poles.get(), 3);
                getBot().sendMessage(chat.getId(), base + " ha hecho la <b>bronce</b> navideña" + emojiSnow + " (cual perdedor)", "html", null, false, messageId, null);
                log.info("Bronce otorgado a " + from.getId() + " en " + chat.getId());
            }
        }

    }

    private void save(PoleCacheManager manager, CachedGroup group, LocalDate today, PoleCollection poles) {
        group.updatePoles(today, poles);
        manager.clearOldCache(group, today);
    }

    private void saveToDatabase(PoleCacheManager manager, PoleAntiCheat antiCheat, CachedGroup group, PoleCollection poles, int updated) {
        CompletableFuture.runAsync(() -> {
            manager.savePoleToDatabase(group, poles, updated);
            antiCheat.checkSuspiciousBehaviour(group, poles, updated);
        });
    }
}
