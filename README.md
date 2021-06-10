# CadiBot [![Build Status](https://travis-ci.com/Cadiducho/CadiBot.svg?branch=development)](https://travis-ci.com/Cadiducho/CadiBot) [![License](https://img.shields.io/github/license/Cadiducho/CadiBot.svg)](https://github.com/Cadiducho/CadiBot/blob/development/LICENSE.md)

[CadiBot](https://telegram.me/Cadibot?start=hola) es un bot de Telegram que contiene una serie de utilidades, principalmente para grupos

# Colaborar
Cualquier aporte será bienvenido :D

Puedes [aportar ideas](https://github.com/Cadiducho/CadiBot/issues/new?template=feature_request.md), [reportar errores](https://github.com/Cadiducho/CadiBot/issues/new?template=bug_report.md) o bien puedes hacer un fork y programar una nueva funcionalidad.

### Compilación
```
$ gradlew build
```
## Modularidad
CadiBot desde la versión 2.0 contiene un núcleo principal y una serie de módulos que se añaden a su alrededor.
Para crear un módulo nuevo, debes crear un nuevo package dentro de `com.cadiducho.bot.modules`, y una clase principal (por convenio, `MiFuncionalidadModule`)
Esa clase debe implementar `Module` y la información acorde a este quedará registrada mediante la anotación `ModuleInfo`
```
@ModuleInfo(name = "MiFuncionalidad", description = "Breve descripción de qué hará mi módulo")
public class MiFuncionalidadModule implements ZinciteModule {
    @Override
    public void onLoad() {
        //qué ejecutar cuando carga el módulo, por ejemplo: registrar comandos
    }

    @Override
    public void onClose() {
        //qué hacer cuando se descarga el módulo, por ejemplo: cerrar procesos secundarios, conexiones a bases de datos...
    }
}
```
Si quieres realizar un módulos externo, puedes compilar todas tus clases en un nuevo archivo .jar y moverlo a la subcarpeta `/modules` que se crea donde ejecuta el bot. Este módulo.jar cargará automáticamente.


## Comandos
Los comandos que se creen deben estar asociados siempre a un módulo.

Existen diferentes maneras de crear un comando.
### Comandos simples 
El sistema del bot soporta la creación de comandos simples mediante una sintaxis Json.

Entenderemos por comandos simples que estos reciban un comando, ignorando los argumentos, y respondan con una o una serie de funcionalidades simples entre las que se incluyen Texto, Gifs, Foto etc.

Este es un ejemplo de un comando simple que responderá a "hola" y "adios" con "ciao"
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
[Aquí](https://github.com/Cadiducho/CadiBot/tree/development/src/main/resources/commands) puedes obtener más información sobre los comandos simples