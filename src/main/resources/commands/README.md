## Comandos simples de Cadibot

Estos comandos son leídos a través de una sintaxis Json y cargados a su módulo correspondientes.

Ejemplo simple de un comando:
```
{
    "aliases": ["hola", "adios"],
    "description": "Saludar o despedirse",
    "module": "Italian Module",
    "funcionalities": [
        {
            "type": "text",
            "text": "ciao",
            "reply_to": "none"
        }
    ]
}
```

### Datos descriptivos
Aliases debe de ser una lista que contenga una o más cadenas de texto a las que el comando responderá, es decir, alias del comando. Estas alias pueden tener espacios en ellas, como "emosido engañado".

Descripción es una breve frase que representa al comando. Esta descripción es usada en los usos y los comandos de ayuda.

Module debe de ser el nombre del módulo al que el comando está asociado

### Funcionalities
Funcionalities es una lista de funcionalidades con las que el bot responderá a esas alias del comando. Las funcionalidades pueden ser 1 o varias. En caso de haber alias, se responderá aleatoriamente con sólo una.

Hay varios tipos de funcionalidades:

#### Text
Responderá con un texto
```
{
  "type": "text",
  "text": "Hola!",
  "reply_to": "none"
}
```

#### Image
Responderá con una imagen/foto
```
{
  "type": "image",
  "image_id": "222222",
  "reply_to": "none"
}
```

#### Gif
Responderá con un gif
```
{
  "type": "gif",
  "gif_id": "222222",
  "reply_to": "none"
}
```

#### Video
Responderá con un video
```
{
  "type": "video",
  "video_id": "222222",
  "reply_to": "none"
}
```

#### Voice
Responderá con una nota de voz
```
{
  "type": "voice",
  "voice_id": "222222",
  "reply_to": "none"
}
```

#### Patrón de respuestas de las funcionalidades
En cada funcionalidad, podrás indicar mediante `reply_to` a qué mensaje de Telegram esponderá el bot
- `none` No responderá a nadie, enviará un mensaje simple
- `original` Responderá al mensaje que invocó el comando
- `answered` Responderá al mensaje que respondía el mensaje que invocó el comando, si este existe

##### Ejemplo:
Se envía un mensaje `Arbol` y este mensaje `Arbol` es respondido con un comando `/Barco`.
Lo que sea que envíe será respondido:
 - `none` A nadie
 - `original` Al que invocó el comando, `/Barco`
 - `answered` Al mensaje que fue respondido, `Arbol`