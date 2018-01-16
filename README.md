# rcon-discord
Ein kleiner Bot für den großen Noah.

# How to install.

- Head to over to [Release][1] and Download the latest `.jar` file
- In the directory you want to run the bot. Create a `config.json` with the following content: 

```
{
  "rcon": {
    "server": "YOUR MINECRAFT SERVER",
    "port": 25575 /*or whatever rcon port you are using. look into your server.properties */,
    "password": "RCON-PASSWORD" /* yet again -> server.properties */
  },
  "selfinvite": true, /* Whether user can use this or only admins. */
  "discord": {
    "admins": [
      "YOUR DISCORD USER ID. Find out using Developer mode.",
      "A SECOND ID"
    ],
    "token": "Your bots token. Create one over at https://discordapp.com/developers/applications/me"
  }
}
```
-  After that run the jar using double click or `java -jar <filename.jar>`


[1]: https://github.com/romangraef/rcon-discord/releases
