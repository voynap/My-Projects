package com.telegram.mafiabot.util;

import java.util.ArrayList;
import java.util.List;

public class TelegramStickersFields {
    public static final TelegramSticker SHERLOCK_CLOWN_PEPE = new TelegramSticker("CAACAgIAAxUAAWQLD40hOMWCzVIT3Uh72Ui4Sv6lAALYAAOrV8QLErq7V-AW7ikvBA");

    public static final TelegramSticker PEPE_GUN = new TelegramSticker("CAACAgQAAxUAAWQLDcDNaNHac9AWbopx5QKowQUWAAItAQAC1m7CGefzNuMHC6C-LwQ");

    public static final TelegramSticker TARANTINO_KISS = new TelegramSticker("CAACAgIAAxkBAAIBZmQO5tTFlOUbBsRF6HOlH0XDo_HfAAJxCgACLw_wBmAumvOEzb9MLwQ");
    public static final TelegramSticker TARANTINO_BLOOD = new TelegramSticker("CAACAgIAAxkBAANHZAsPB5VDK5FoLZoa_CnmSjtk2xgAAoIKAAIvD_AGK5wlkYQ8c2cvBA");
    public static final TelegramSticker test = new TelegramSticker("CAACAgIAAxkBAAIBdGQO7VqbyECfh0BywqX14KI75IgTAAKjCgAC_564SH9OyX2bl68TLwQ");

    public static final TelegramSticker TARANTINO_HELLO = new TelegramSticker("CAACAgIAAxkBAAM4ZAsO_fvGGGY9vUssgQvcBrn7NHUAAnUKAAIvD_AGEb5N7hzlEhovBA");

    public static final TelegramSticker RICK_HELLO = new TelegramSticker("CAACAgIAAxUAAWQO60kq-_338BVgAWKjGAI7cXgSAAInAwACtXHaBg_03s-gxd3sLwQ");

    public static final TelegramSticker RICK_DETECTIVE = new TelegramSticker("CAACAgIAAxUAAWQO60m_fBPSn9CIXma3B84YYKxsAAI7AwACtXHaBhhLBtJVU8tELwQ");

    public static final TelegramSticker PEPE_CLOWN_HELLO = new TelegramSticker("CAACAgIAAxkBAAIBdGQO7VqbyECfh0BywqX14KI75IgTAAKjCgAC_564SH9OyX2bl68TLwQ");

    public static final TelegramSticker ORANGUTAN_HELLO = new TelegramSticker("CAACAgIAAxUAAWQO7R0q9XtAFrBqulXx7peD8aXVAAI0AQACUomRIxPN12_1HAdYLwQ");

    public static final TelegramSticker RICK_KISS = new TelegramSticker("CAACAgIAAxkBAAIBfWQO7-WbqpxWGuBQ3NsbHQ_gGmgcAAJAAwACtXHaBrkS8NwdVRhcLwQ");








    public static final List<TelegramSticker> HELLO_STICKERS = new ArrayList<>();

    static {
        HELLO_STICKERS.add(TARANTINO_KISS);
        HELLO_STICKERS.add(ORANGUTAN_HELLO);
        HELLO_STICKERS.add(PEPE_CLOWN_HELLO);
        HELLO_STICKERS.add(RICK_HELLO);
        HELLO_STICKERS.add(TARANTINO_HELLO);
        HELLO_STICKERS.add(RICK_KISS);
    }

    public static List<TelegramSticker> getHelloStickers() {
        return HELLO_STICKERS;
    }
}
