#!/usr/bin/python3

__author__ = "blueShard (ByteDream)"
__license__ = "MPL-2.0"
__version__ = "1.1"

# Startscript um zu check, ob python3, pip3 + alle benötigten externen python3 libraries installiert sind (wenn nicht wird das benötigte nachinstalliert), das danach die main.py startet:
"""
#!/bin/bash

which python3 &> /dev/null
[ $? -eq 0 ] || apt-get -y install python3

which pip3 &> /dev/null
[ $? -eq 0 ] || apt-get -y install python3-pip

python3 -c "import aiogram" &> /dev/null
[ $? -eq 0 ] || yes | pip3 install aiogram 1> /dev/null

python3 -c "import discord" &> /dev/null
[ $? -eq 0 ] || yes | pip3 install discord 1> /dev/null

python3 -c "import webuntis" &> /dev/null
[ $? -eq 0 ] || yes | pip3 install webuntis 1> /dev/null

python3 main.py <discord api token> <telegram api token> <webuntis username> <webuntis password>
"""

import asyncio
import discord  # https://github.com/Rapptz/discord.py
import logging
import xml.etree.ElementTree as ET
from aiogram import Bot, Dispatcher, types  # https://github.com/aiogram/aiogram
from datetime import date, datetime, time, timedelta
from math import ceil
from random import choice
from sys import argv
from traceback import format_exc
from webuntis import Session  # https://github.com/python-webuntis/python-webuntis
from xml.dom import minidom

# logging.basicConfig(format="[%(asctime)s] %(levelname)s: %(message)s", level=logging.INFO)
logging.basicConfig(handlers=[logging.StreamHandler(), logging.FileHandler("/var/log/ScheduleAndMoreBot.log", "a+")], format="[%(asctime)s] %(levelname)s: %(message)s", level=logging.INFO)

logging.info("Start logging")


class ScheduleAnMoreBot(discord.Client):
    telegram_bot = Bot(token=argv[2])
    dispatcher = Dispatcher(telegram_bot)

    def __init__(self, ignore_discord: bool = False, **options) -> None:
        super().__init__(**options)
        self.ignore_discord = ignore_discord

        self.info_file = "infos.txt"

        self.discord_utils = DiscordUtils()

        self.discord_channel = None

        self.telegram_utils = TelegramUtils()

        self.dispatcher.register_message_handler(self.telegram_private, commands=["private"])
        self.dispatcher.register_message_handler(self.telegram_example, commands=["example"])
        self.dispatcher.register_message_handler(self.telegram_help, commands=["help"])
        self.dispatcher.register_message_handler(self.telegram_add_info, commands=["add_info"])
        self.dispatcher.register_message_handler(self.telegram_info, commands=["info"])
        self.dispatcher.register_message_handler(self.telegram_source, commands=["src", "source"])

        if self.ignore_discord:
            loop = asyncio.get_event_loop()
            loop.create_task(self.dispatcher.start_polling(self.dispatcher))
            loop.create_task(Checker(None, self.telegram_bot, self.telegram_utils.group_id).main())

    # ----- Discord ----- #

    async def on_ready(self) -> None:
        logging.info("Connected to Discord server")

    async def on_message(self, message: discord.Message) -> None:
        user_input = message.content.lower().strip()
        if not user_input.startswith("$"):
            return
        elif self.discord_channel is None:
            if message.content.lower().strip() == "$start" and message.channel.id == self.discord_utils.channel_id:
                self.discord_channel = message.channel
                await message.channel.send("Der Bot wurde aktiviert")
                if not self.ignore_discord:
                    loop = asyncio.get_event_loop()
                    loop.create_task(self.dispatcher.start_polling(self.dispatcher))
                    loop.create_task(Checker(self.discord_channel, self.telegram_bot, self.telegram_utils.group_id).main())
            else:
                await message.channel.send("Tippe '$start' im richtigen Channel um den Bot zu aktivieren")
        else:
            user_input = user_input[1:]
            # switch-case wäre schon :p
            if user_input == "help":
                await self.discord_help(message)
            elif user_input == "example":
                await self.discord_example(message)
            elif user_input.startswith("add_info"):
                await self.discord_add_info(message)
            elif user_input == "info":
                await self.discord_info(message)
            elif user_input in ["src", "source"]:
                await self.discord_source(message)
            else:
                await message.channel.send("Tippe '$help' für Hilfe")

    async def discord_help(self, message: discord.Message) -> None:
        """Zeigt alle Discord Befehle + Information was diese tuhen an"""
        if self.discord_utils.is_valid_channel(message.channel) or self.discord_utils.is_valid_user(self.discord_channel, message.author):
            await message.channel.send(self.discord_utils.help())
        else:
            await message.channel.send(self.discord_utils.not_permitted())

    async def discord_example(self, message: discord.Message) -> None:
        """Zeigt Beispiele zu allen Discord Befehlen, wie man diese nutzt"""
        if self.discord_utils.is_valid_channel(message.channel) or self.discord_utils.is_valid_user(self.discord_channel, message.author):
            await message.channel.send(self.discord_utils.example())
        else:
            await message.channel.send(self.discord_utils.not_permitted())

    async def discord_add_info(self, message: discord.Message) -> None:
        """Fügt eine neue Info hinzu"""
        if self.discord_utils.is_valid_channel(message.channel) or self.discord_utils.is_valid_user(self.discord_channel, message.author):
            command_no_space = message.content.replace(" ", "")
            infos = Infos()
            full_date = datetime.today()
            today = datetime(full_date.year, full_date.month, full_date.day)  # hier wird auf die genau Uhrzeit verzichtet, damit man noch anträge für den selben Tag erstellen kann
            date_for_info = command_no_space[9:19].split("-")

            for index, x in enumerate(date_for_info):
                if x.startswith("0"):
                    date_for_info[index] = x[1:]

            try:
                if today > datetime(int(date_for_info[2]), int(date_for_info[1]), int(date_for_info[0])):
                    await message.channel.send("Das Datum liegt in der Vergangenheit")
                    return
                else:
                    date = command_no_space[9:19]
                    information = message.content.replace("$add_info", "", 1).replace(command_no_space[9:19], "", 1).strip()
                    infos.addappend(date, information)
                    for embed in self.discord_utils.embed_info(date, information):
                        await self.discord_channel.send(embed=embed)
                    await self.telegram_bot.send_message(self.telegram_utils.group_id, "Eine neue Info für " + date + " wurde hinzugefügt: " + information)
                    logging.info("New entry for date " + date + " was added: " + information)
            except (IndexError, SyntaxError, ValueError):
                await message.channel.send("Es wurde kein richtiges Datum angegeben")
                logging.warning("An error occurred while trying to add a new information:\n" + format_exc())

    async def discord_info(self, message: discord.Message) -> None:
        """Zeigt alle Infos an"""
        if self.discord_utils.is_valid_channel(message.channel) or self.discord_utils.is_valid_user(self.discord_channel, message.author):
            infos = Infos()

            all_colors = [discord.Color.blue(),
                          discord.Color.blurple(),
                          discord.Color.dark_blue(),
                          discord.Color.dark_gold(),
                          discord.Color.darker_grey(),
                          discord.Color.dark_green(),
                          discord.Color.dark_grey(),
                          discord.Color.dark_magenta(),
                          discord.Color.dark_orange(),
                          discord.Color.dark_purple(),
                          discord.Color.dark_red(),
                          discord.Color.dark_teal(),
                          discord.Color.default()]
            choosed_colors = []

            for child in infos.root:
                info = infos.get(child.tag)
                separator = info.split("~", 1)[0]
                day_infos = info.replace("~", "", 1).split(separator)[1:]

                if len(choosed_colors) >= len(all_colors):
                    choosed_colors = []
                color = choice(all_colors)
                while color in choosed_colors:
                    color = choice(all_colors)

                discord_info = discord.Embed(title="Infos für " + child.tag[1:], color=color)
                #  discord_info.set_image(url="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Infobox_info_icon.svg/2000px-Infobox_info_icon.svg.png")
                discord_info.set_thumbnail(url="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Infobox_info_icon.svg/2000px-Infobox_info_icon.svg.png")
                for index, day_info in enumerate(day_infos):
                    if len(day_info) > 1000:
                        for x in range(0, ceil(len(day_info) / 1000)):
                            if x % 6:
                                await message.channel.send(embed=discord_info)
                                discord_info.clear_fields()
                            discord_info.add_field(name=str(index + 1) + "/" + str(x), value=day_info[x * 1000:(x + 1) * 1000], inline=False)
                    else:
                        discord_info.add_field(name=str(index + 1), value=day_info, inline=False)

                await message.channel.send(embed=discord_info)

    async def discord_source(self, message: discord.Message) -> None:
        """Stellt den Source Code zu Verfügung"""
        await message.channel.send(file=discord.File("main.py", filename="main.py"))

    # ----- Telegram ----- #

    async def telegram_private(self, message: types.Message) -> None:
        """Fügt einen Telegram Nutzer zur liste hinzu, damit dieser per DM mit dem Bot interagieren"""
        if self.telegram_utils.is_valid_group(message.chat):
            if not self.telegram_utils.is_private_user(message.from_user):
                user_id = message.from_user.id
                self.telegram_utils.private_users_id.append(user_id)
                with open(self.telegram_utils.private_users_file, "a+") as file:
                    file.write(str(user_id) + ";")
                    file.close()
                await message.answer("Neuer Nutzer wurde eingetragen")
                logging.info("New private telegram user registered")
        else:
            await message.answer(self.telegram_utils.not_permitted())

    async def telegram_help(self, message: types.Message) -> None:
        """Zeigt alle Telegram Befehle + Information was diese tuhen an"""
        if self.telegram_utils.is_valid_group(message.chat) or self.telegram_utils.is_private_user(message.from_user):
            await message.answer(self.telegram_utils.help(), parse_mode="MarkdownV2")
        else:
            await message.answer(self.telegram_utils.not_permitted())

    async def telegram_example(self, message: types.Message) -> None:
        """Zeigt Beispiele zu allen Telegram Befehlen, wie man diese nutzt"""
        if self.telegram_utils.is_valid_group(message.chat) or self.telegram_utils.is_private_user(message.from_user):
            await message.answer(self.telegram_utils.example(), parse_mode="MarkdownV2")
        else:
            await message.answer(self.telegram_utils.not_permitted())

    async def telegram_add_info(self, message: types.Message) -> None:
        """Fügt eine neue Info hinzu"""
        if self.telegram_utils.is_valid_group(message.chat) or self.telegram_utils.is_private_user(message.from_user):
            infos = Infos()
            message_no_space = message.text.replace(" ", "")
            full_date = datetime.today()
            today = datetime(full_date.year, full_date.month, full_date.day)  # hier wird auf die genau Uhrzeit verzichtet, damit man noch anträge für den selben Tag erstellen kann
            date_for_info = message_no_space[9:19].split("-")
            for index, x in enumerate(date_for_info):
                if x.startswith("0"):
                    date_for_info[index] = x[1:]

            try:
                if today > datetime(int(date_for_info[2]), int(date_for_info[1]), int(date_for_info[0])):
                    await message.answer("Das Datum liegt in der Vergangenheit")
                    return
                else:
                    date = message_no_space[9:19]
                    information = message.text.replace("/add_info", "", 1).replace(date, "", 1).strip()
                    infos.addappend(date, information)
                    await self.telegram_bot.send_message(self.telegram_utils.group_id, "Eine neue Info für " + date + " wurde hinzugefügt: " + information)
                    for embed in self.discord_utils.embed_info(date, information):
                        await self.discord_channel.send(embed=embed)
                    logging.info("New entry for date " + date + " was added: " + information)
            except (IndexError, SyntaxError, ValueError):
                await message.answer("Es wurde kein richtiges Datum angegeben")
        else:
            await message.answer(self.telegram_utils.not_permitted())

    async def telegram_info(self, message: types.Message) -> None:
        """Zeigt alle Infos an"""
        if self.telegram_utils.is_valid_group(message.chat) or self.telegram_utils.is_private_user(message.from_user):
            infos = Infos()
            information = ""

            for child in infos.root:
                info = infos.get(child.tag)
                info.replace(info.split("~", 1)[0], "\n\n")
                information = information + child.tag[1:] + ": " + info.split("~", 1)[1]
                await message.answer(information)
                information = ""
        else:
            await message.answer(self.telegram_utils.not_permitted())

    async def telegram_source(self, message: types.Message) -> None:
        """Stellt den Source Code zu Verfügung"""
        if self.telegram_utils.is_valid_group(message.chat) or self.telegram_utils.is_private_user(message.from_user):
            await message.answer_document(document=open("main.py", "rb"))
        else:
            await message.answer(self.telegram_utils.not_permitted())


class DiscordUtils:

    def __init__(self) -> None:
        self.channel_id = 746369803941576784
        # Test: 746477001237594174

    def embed_info(self, date, info) -> list:
        """Erstellt discord embeds für die gegeben info"""
        return_list = []
        all_colors = [discord.Color.blue(),
                      discord.Color.blurple(),
                      discord.Color.dark_blue(),
                      discord.Color.dark_gold(),
                      discord.Color.darker_grey(),
                      discord.Color.dark_green(),
                      discord.Color.dark_grey(),
                      discord.Color.dark_magenta(),
                      discord.Color.dark_orange(),
                      discord.Color.dark_purple(),
                      discord.Color.dark_red(),
                      discord.Color.dark_teal(),
                      discord.Color.default()]
        choosed_colors = []

        if len(choosed_colors) >= len(all_colors):
            choosed_colors = []
        color = choice(all_colors)
        while color in choosed_colors:
            color = choice(all_colors)

        discord_info = discord.Embed(title="Eine neue Info für " + date + " wurde hinzugefügt", color=color)
        #  discord_info.set_image(url="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Infobox_info_icon.svg/2000px-Infobox_info_icon.svg.png")
        discord_info.set_thumbnail(url="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Infobox_info_icon.svg/2000px-Infobox_info_icon.svg.png")
        if len(info) > 1000:
            for x in range(0, ceil(len(info) / 1000)):
                if x % 6:
                    return_list.append(discord_info)
                    discord_info.clear_fields()
                discord_info.add_field(name="Info" + "/" + str(x), value=info[x * 1000:(x + 1) * 1000], inline=False)
        else:
            discord_info.add_field(name="Info", value=info, inline=False)

        return_list.append(discord_info)

        return return_list

    def example(self) -> str:
        """Discord Text, der Beispiele zu allen Befehlen zeigt"""
        example_text = "```\n" \
                       "$start                          $start\n" \
                       "$help                           $help\n" \
                       "$example                        $example\n" \
                       "$add_info [dd-mm-yyyy] [info]   $add_info 01-01-2222 Eine einfache test Info\n" \
                       "$info                           $info\n" \
                       "$src / $source                  $src\n" \
                       "```"
        return example_text

    def help(self) -> str:
        """Discord Text, der Hilfe zu allen Befehlen zeigt"""
        help_text = "```\n" \
                    "DM (direct message) = Nur per Direktnachticht ausführbar\n" \
                    "SC (source channel) = Nur vom Channel von dem aus der Bot gestartet wurde ausführbar\n" \
                    "EV (everywhere)     = Von überall ausführbar\n\n" \
                    "Befehlsname            Von wo ausführbar   Beschreibung\n\n" \
                    "$start                        SC           Startet den Bot\n\n" \
                    "$help                         EV           Zeigt Hilfe zu den vorhanden Befehlen an\n" \
                    "$example                      EV           Zeigt beispiele für jeden Befehl\n" \
                    "$add_info [dd-mm-yyyy] [info] EV           Fügt neue Informationen zu einem bestimmten Tag hinzu\n" \
                    "$info                         EV           Gibt eingetragene infos wieder\n" \
                    "$src / $source                EV           Stellt die Datei mit dem Quellcode zu Verfügung\n" \
                    "```"
        return help_text

    def is_valid_channel(self, channel: discord.TextChannel) -> bool:
        """Checkt, ob der gegebene Channel der Channel ist, auf dem der Bot aktiv sein soll"""
        try:
            if channel.id == self.channel_id:
                return True
            else:
                return False
        except AttributeError:
            return False

    def is_valid_user(self, channel: discord.TextChannel, user: discord.User) -> bool:
        """Überprüft, ob der Nutzer auf dem Discord Server Mitglied ist"""
        print(user.id, channel.members)
        try:
            for member in channel.members:
                if user.id == member.id:
                    return True
        except AttributeError:
            logging.warning("Attribute error occurred while trying to check if discord user is valid")

        return False

    def not_permitted(self) -> str:
        """Info, wenn eine nicht berechtigte Person einen Discord Befehl ausführt"""
        return "Nur Personen, die Mitglieder auf dem Discord Server sind, haben Zugriff auf die Befehle"


class TelegramUtils:

    def __init__(self) -> None:
        self.group_id = -384078711
        self.private_users_file = "private_users.scv"
        self.private_users_id = open(self.private_users_file, "r+").readline().split(";")

    def example(self) -> str:
        """Telegram Text, der Beispiele zu allen Befehlen zeigt"""
        example_text = "```\n" \
                       "/start\n" \
                       "/start\n\n" \
                       "/help\n" \
                       "/help\n\n" \
                       "/example\n" \
                       "/example\n\n" \
                       "/add_info [dd-mm-yyyy] [info]\n" \
                       "/add_info 01-01-2222 Eine einfache test Info\n\n" \
                       "/info\n" \
                       "/info\n\n" \
                       "/src or /source\n" \
                       "/src\n" \
                       "```"
        return example_text

    def help(self) -> str:
        """Discord Text, der Hilfe zu allen Befehlen zeigt"""
        help_text = "```\n" \
                    "DM (direct message) = Nur per Direktnachticht ausführbar\n" \
                    "GR (group) = Nur vom Channel von dem aus der Bot gestartet wurde ausführbar\n" \
                    "EV (everywhere) = Von überall ausführbar\n\n" \
                    "/private\n" \
                    "GR\n" \
                    "Nutzer bekommt Zugriff auf Befehle, die per DM ausgeführt werden können\n\n\n" \
                    "/help\n" \
                    "EV\n" \
                    "Zeigt Hilfe zu den vorhanden Befehlen an\n\n" \
                    "/example\n" \
                    "EV\n" \
                    "Zeigt Hilfe zu den vorhanden Befehlen an\n\n" \
                    "/add_info [dd-mm-yyyy] [info]\n" \
                    "EV\n" \
                    "Fügt neue Informationen zu einem bestimmten Tag hinzu\n\n" \
                    "/info\n" \
                    "EV\n" \
                    "Gibt eingetragene Infos wieder\n\n\n" \
                    "/src or /source\n" \
                    "EV\n" \
                    "Stellt die Datei mit dem Quellcode zu Verfügung\n" \
                    "```"
        return help_text

    def is_private_user(self, user: types.User) -> bool:
        """Überprüft, ob der Nutzer '/private' in der Gruppe eingegeben hat"""
        if str(user.id) in self.private_users_id:
            return True
        else:
            return False

    def is_valid_group(self, chat: types.Chat) -> bool:
        """Checkt, ob die gegeben Gruppe die Gruppe ist, worin der Bot aktiv sein soll"""
        if chat.id == self.group_id:
            return True
        else:
            return False

    def not_permitted(self) -> str:
        """Info, wenn eine nicht berechtigte Person einen Telegram Befehl ausführt"""
        return "Gebe '/private' in der Gruppe ein, um Zugriff auf Befehle, die per DM ausgeführt werden können, zu erhalten"


class Infos:  # wird eventuell in Zukunft durch ein lua programm ersetzt

    def __init__(self, info_file: str = "infos.xml") -> None:
        self.info_file = info_file
        self.root = ET.fromstring("".join([item.replace("\n", "").strip() for item in [line for line in open(info_file, "r")]]))

    def __create_separator(self, text: str) -> str:
        """Erstellt ein separator"""
        indicator = "^|^"
        choices = ("§", "!", "^")

        while True:
            if indicator in text:
                list_choice = choice(choices)
                splitted_indicator = indicator.split("|")
                indicator = splitted_indicator[0] + list_choice + "|" + list_choice + splitted_indicator[1]
            else:
                return indicator

    def _prettify(self, string: str = None) -> str:
        """Macht den XML Tree lesbarer für Menschis^^"""
        if string is None:
            reparsed = minidom.parseString(ET.tostring(self.root, "utf-8"))
        else:
            reparsed = minidom.parseString(string)
        pre_output = reparsed.toprettyxml(indent="  ")
        return "\n".join(pre_output.split("\n")[1:])

    def addappend(self, date_: str, text: str) -> None:
        """Fügt einen neuen Eintrag zum gegebenen Datum hinzu"""
        date_ = "_" + date_
        for child in self.root:
            if child.tag == date:
                child_text = child.text
                old_separator = child.attrib["separator"]
                new_separator = self.__create_separator(child_text + text)
                child.text = child.text.replace(old_separator, new_separator) + new_separator + text
                child.attrib["separator"] = new_separator
                self.write()
                return

        new_entry = ET.Element(date_)
        new_entry.text = text
        new_entry.attrib["separator"] = self.__create_separator(text)

        self.root.append(new_entry)
        self.write()

    def delete(self, date_: str) -> None:
        """Löscht alle Einträge an dem gegeben Datum"""
        for child in self.root:
            if child.tag == date_:
                self.root.remove(child)
                self.write()
                return

    def get(self, date_: str) -> str:
        """Gibt alle Einträge an dem gegeben Datum zurück"""
        for child in self.root:
            if child.tag == date_:
                return child.attrib["separator"] + "~" + child.text

        return ""

    def write(self) -> None:
        """Schreibt den XML Tree in die Datei"""
        with open(self.info_file, "w+") as file:
            file.write(self._prettify())
            file.close()


class Checker:

    def __init__(self, discord_channel: discord.TextChannel, telegram_bot: Bot, telegram_group_id: int):
        self.discord_channel = discord_channel
        self.telegram_bot = telegram_bot
        self.telegram_group_id = telegram_group_id

        self.lessons = {"1": [time(8, 0,), time(8, 45)],
                        "2": [time(8, 45), time(9, 30)],
                        "3": [time(9, 45), time(10, 30)],
                        "4": [time(10, 30), time(11, 15)],
                        "5": [time(11, 45), time(12, 30)],
                        "6": [time(12, 30), time(13, 15)],
                        "7": [time(13, 30), time(14, 15)],
                        "8": [time(14, 15), time(15, 0)]}

        self.all_cancelled_lessons_thursday = {}
        self.all_ignored_lessons_thursday = {}
        self.which_thursday = date.today()
        self.all_cancelled_lessons_friday = {}
        self.all_ignored_lessons_friday = {}
        self.which_friday = date.today()

        self.session: Session = None

    async def __check_and_send_cancelled_lessons(self, date_to_check: date) -> None:  # die methode ist etwas schwer zu lesen
        """Überprüft, ob Stunden ausfallen / verlegt wurden und gibt das Ergebnis (wenn es eins gibts) in Discord und Telegram wieder"""
        try:
            embed = None
            all_embed_fields = {}
            all_telegram_messages = {}
            telegram_message = ""

            if date_to_check.weekday() == 3:
                already_cancelled_lessons: dict = self.all_cancelled_lessons_thursday
                all_ignored_lessons: dict = self.all_ignored_lessons_thursday
                weekday_in_german = "Donnerstag"
            elif date_to_check.weekday() == 4:
                already_cancelled_lessons: dict = self.all_cancelled_lessons_friday
                all_ignored_lessons: dict = self.all_ignored_lessons_friday
                weekday_in_german = "Freitag"
            else:
                raise ValueError('date_to_check (datetime.date) must be thursday or friday')
            timetable = self.session.timetable(start=date_to_check, end=date_to_check, klasse=2015)

            for lesson in timetable:
                lesson_number = str(lesson.start.time().strftime("%H:%M")) + " Uhr - " + str(lesson.end.time().strftime("%H:%M") + " Uhr")
                for lesson_num, lesson_time in self.lessons.items():
                    if lesson_time[0] == lesson.start.time():
                        lesson_number = lesson_num
                        break

                embed_title = "Stunden Ausfall Information für " + weekday_in_german + ", den " + date_to_check.strftime("%d.%m.%Y")

                if lesson.code == "irregular" and lesson_number not in all_ignored_lessons.keys() and lesson.teachers not in all_ignored_lessons.values():
                    embed = discord.Embed(title=embed_title, color=discord.Color.from_rgb(77, 255, 77))
                    for lesson1 in timetable:
                        if lesson.teachers == lesson1.teachers and lesson.start is not lesson1.start and lesson1.code == "cancelled":
                            lesson1_number = str(lesson.start.time().strftime("%H:%M")) + " Uhr - " + str(lesson.end.time().strftime("%H:%M") + " Uhr")
                            for lesson_num, lesson_time in self.lessons.items():
                                if lesson_time[0] == lesson1.start.time():
                                    lesson1_number = lesson_num
                                    break

                            for number in list(all_embed_fields.keys()):  # wenn es ohne list gemacht werden würde, würde ein RuntimeError kommen
                                if number in [lesson_number, lesson1_number]:
                                    del all_embed_fields[number]
                                    del all_telegram_messages[number]

                            if len(lesson1_number) == 1:
                                all_embed_fields[lesson_number] = {lesson1_number + ". Stunde wurde zur " + lesson_number +
                                                                   ". Stunde umverlegt": "Die " + lesson1_number + ". Stunde (" + lesson1.start.time().strftime("%H:%M") + " Uhr - " + lesson1.end.time().strftime("%H:%M") + " Uhr) bei " + \
                                                                   ", ".join([teacher.long_name for teacher in lesson.teachers]) + " wurde zur " + lesson_number + ". Stunde (" + lesson.start.time().strftime("%H:%M") + \
                                                                   " Uhr - " + lesson.end.time().strftime("%H:%M") + " Uhr) umverlegt"}
                                all_telegram_messages[lesson_number] = "Die " + lesson1_number + ". Stunde (" + lesson1.start.time().strftime("%H:%M") + " Uhr - " + lesson1.end.time().strftime("%H:%M") + " Uhr) bei " + \
                                                                       ", ".join([teacher.long_name for teacher in lesson.teachers]) + " wurde zur " + lesson_number + ". Stunde (" + lesson.start.time().strftime("%H:%M") + \
                                                                       " Uhr - " + lesson.end.time().strftime("%H:%M") + " Uhr) umverlegt"
                            else:
                                all_embed_fields[lesson_number] = {"Die Stunde " + lesson1_number + " wurde zur Stunde" + lesson_number +
                                                                   " umverlegt": "Die Stunde " + lesson1_number + " bei " + ", ".join([teacher.long_name for teacher in lesson.teachers]) + " wurde zur Stunde " + lesson_number + " umverlegt"}
                                all_telegram_messages[lesson_number] = "Die Stunde " + lesson1_number + " bei " + ", ".join([teacher.long_name for teacher in lesson.teachers]) + " wurde zur Stunde " + lesson_number + " umverlegt"

                            all_ignored_lessons[lesson_number] = lesson.teachers
                            all_ignored_lessons[lesson1_number] = lesson.teachers
                elif lesson.code == "cancelled":
                    embed = discord.Embed(title=embed_title, color=discord.Color.from_rgb(255, 0, 0))
                    if lesson_number not in already_cancelled_lessons.keys() and lesson_number not in all_ignored_lessons.keys():
                        already_cancelled_lessons[lesson_number] = lesson.teachers
                        if len(lesson_number) == 1:
                            all_embed_fields[lesson_number] = {"Ausfall " + str(lesson_number) + ". Stunde (" + lesson.start.time().strftime("%H:%M") + " Uhr - " +
                                                               lesson.end.time().strftime("%H:%M") + " Uhr)": "Ausfall bei " + ", ".join([teacher.long_name for teacher in lesson.teachers]) + " in " +
                                                                                                              ", ".join([subject.long_name for subject in lesson.subjects])}
                            all_telegram_messages[lesson_number] = "Ausfall am " + weekday_in_german + ", den " + date_to_check.strftime("%d.%m.%Y") + " in der " + lesson_number + " Stunde bei " +\
                                                                   ", ".join([teacher.long_name for teacher in lesson.teachers]) + " in " + ", ".join([subject.long_name for subject in lesson.subjects]) + "\n\n"
                        else:
                            all_embed_fields[lesson_number] = {"Ausfall " + lesson_number: "Ausfall bei " + ", ".join([teacher.long_name for teacher in lesson.teachers]) + " in " + ", ".join([subject.long_name for subject in lesson.subjects])}
                            all_telegram_messages[lesson_number] = "Ausfall " + lesson_number + " am " + weekday_in_german + ", den " + date_to_check.strftime("%d.%m.%Y") + " bei " +\
                                                                   ", ".join([teacher.long_name for teacher in lesson.teachers]) + " in " + ", ".join([subject.long_name for subject in lesson.subjects]) + "\n\n"
                elif lesson_number in already_cancelled_lessons.keys():
                    embed = discord.Embed(title=embed_title, color=discord.Color.from_rgb(77, 255, 77))
                    if lesson.teachers in already_cancelled_lessons.values():
                        del already_cancelled_lessons[lesson_number]
                        if len(lesson_number) == 1:
                            all_embed_fields[lesson_number] = {"KEIN Ausfall " + str(lesson_number) + ". Stunde (" + lesson.start.time().strftime("%H:%M") + " Uhr - " +
                                                               lesson.end.time().strftime("%H:%M") + " Uhr)": "KEIN Ausfall bei " + ", ".join([teacher.long_name for teacher in lesson.teachers]) + " in " +
                                                                                                              ", ".join([subject.long_name for subject in lesson.subjects])}
                            all_telegram_messages[lesson_number] = "KEIN Ausfall am " + weekday_in_german + ", den " + date_to_check.strftime("%d.%m.%Y") + " in der " + lesson_number + " Stunde bei " + \
                                                                   ", ".join([teacher.long_name for teacher in lesson.teachers]) + " in " + ", ".join([subject.long_name for subject in lesson.subjects]) + "\n\n"
                        else:
                            all_embed_fields[lesson_number] = {"KEIN Ausfall " + lesson_number: "KEIN Ausfall bei " + ", ".join([teacher.long_name for teacher in lesson.teachers]) + " in " +
                                                                                                ", ".join([subject.long_name for subject in lesson.subjects])}
                            all_telegram_messages[lesson_number] = "KEIN Ausfall " + lesson_number + " am " + weekday_in_german + ", den " + date_to_check.strftime("%d.%m.%Y") + " bei " +\
                                                                   ", ".join([teacher.long_name for teacher in lesson.teachers]) + " in " + ", ".join([subject.long_name for subject in lesson.subjects]) + "\n\n"

            if date_to_check.weekday() == 3:
                self.all_cancelled_lessons_thursday = already_cancelled_lessons
                self.all_ignored_lessons_thursday = all_ignored_lessons
            elif date_to_check.weekday() == 4:
                self.all_cancelled_lessons_friday = already_cancelled_lessons
                self.all_ignored_lessons_friday = all_ignored_lessons

            if len(all_telegram_messages) != 0 and len(all_embed_fields) != 0:
                for number, content in all_embed_fields.items():
                    embed.add_field(name=list(content.keys())[0], value=list(content.values())[0])
                    telegram_message += all_telegram_messages[number]
                await self.discord_channel.send(embed=embed)
                await self.telegram_bot.send_message(self.telegram_group_id, telegram_message)
                logging.info("Send message(s) (content from telegram message): " + telegram_message.replace("\n\n", "\n"))
        except Exception:
            logging.warning("An unexpected error occured, while trying to check the schedule\n" + format_exc())
            await self.discord_channel.send("Ein Fehler trat auf, während der Stundenplan auf Veränderungen überprüft wurde. Siehe Logs für Details")
            await self.telegram_bot.send_message(self.telegram_group_id, "Ein Fehler trat auf, während der Stundenplan auf veränderungen überprüft wurde. Siehe Logs für Details")

    async def main(self, check_time: int = 60 * 60) -> None:
        """Überprüft nach einer gewissen Zeit immer wieder, ob veraltete Infos exestieren"""
        try:
            self.session = Session(server="asopo.webuntis.com",
                                   username=argv[3],
                                   password=argv[4],
                                   school="Konrad-Zuse-schule",
                                   useragent="")
            try:
                self.session.login()
            except Exception as e:
                logging.warning("A login error occurred (" + "\n".join([arg for arg in e.args]) + ")")
                await self.discord_channel.send("Ein (Web)Untis Loginfehler ist aufgetreten. Siehe Logs für Details")
                await self.telegram_bot.send_message(self.telegram_group_id, "Ein (Web)Untis Loginfehler ist aufgetrten. Siehe Logs für Details")
        except IndexError:
            logging.warning("No username and / or password for webuntis is / are given")
            await self.discord_channel.send("Ein (Web)Untis Loginfehler ist aufgetreten. Siehe Logs für Details")
            await self.telegram_bot.send_message(self.telegram_group_id, "Ein (Web)Untis Loginfehler ist aufgetrten. Siehe Logs für Details")
        except Exception:
            logging.warning("An exception for the webuntis session occurred:\n" + format_exc())
            await self.discord_channel.send("Ein (Web)Untis Loginfehler ist aufgetreten. Siehe Logs für Details")
            await self.telegram_bot.send_message(self.telegram_group_id, "Ein (Web)Untis Loginfehler ist aufgetrten. Siehe Logs für Details")

        while True:
            if self.session is not None:
                today = date.today()
                today_weekday = today.weekday()
                if today_weekday == 3:  # donnerstag
                    await self.__check_and_send_cancelled_lessons(today + timedelta(days=1))

                    if datetime.now().hour > 12:  # wenn es über 12 uhr ist, wird angefangen nach ausfall in der nächsten woche zu suchen
                        if self.which_thursday < today:
                            self.all_cancelled_lessons_thursday = {}
                            self.all_ignored_lessons_thursday = {}
                            await self.__check_and_send_cancelled_lessons(today + timedelta(days=7))
                        else:
                            await self.__check_and_send_cancelled_lessons(today + timedelta(days=7))
                    else:
                        await self.__check_and_send_cancelled_lessons(today)

                elif today_weekday == 4:  # freitag
                    await self.__check_and_send_cancelled_lessons(today + timedelta(days=6))

                    if datetime.now().hour > 12:  # wenn es über 12 uhr ist, wird angefangen nach ausfall in der nächsten woche zu gucken
                        if self.which_friday < today:
                            self.all_cancelled_lessons_friday = {}
                            self.all_cancelled_lessons_friday = {}
                            await self.__check_and_send_cancelled_lessons(today + timedelta(days=7))
                        else:
                            await self.__check_and_send_cancelled_lessons(today + timedelta(days=7))
                    else:
                        await self.__check_and_send_cancelled_lessons(today)

                else:
                    for day in range(1, 6):
                        new_day = today + timedelta(days=day)
                        if new_day.weekday() in [3, 4]:
                            await self.__check_and_send_cancelled_lessons(new_day)

            try:
                infos = Infos()
                today = datetime.today()
                for child in infos.root:
                    child_date = child.tag[1:].split("-")
                    for index, x in enumerate(child_date):
                        if x.startswith("0"):
                            child_date[index] = x[1:]
                    if today > datetime(int(child_date[2]), int(child_date[1]), int(child_date[0]) + 1):
                        infos.delete(child.tag)
                        logging.info("Removed informations for day " + child.tag)
                logging.info("Checked for old informations")
            except Exception:
                logging.warning("An unexpected error occured, while trying to check the infos\n" + format_exc())
                await self.discord_channel.send("Ein Fehler trat auf, während die Infos Datei auf alte Daten überprüft wurde. Siehe Logs für Details")
                await self.telegram_bot.send_message(self.telegram_group_id, "Ein Fehler trat auf, während die Infos Datei auf alte Daten überprüft wurde. Siehe Logs für Details")
            await asyncio.sleep(check_time)  # schläft die gegebene Zeit und checkt dann wieder von neuem, ob sich was am Stundenplan geändert hat / ob Infos gelöscht werden können


if __name__ == '__main__':
    schedule_and_more_bot = ScheduleAnMoreBot()
    schedule_and_more_bot.run(argv[1])
