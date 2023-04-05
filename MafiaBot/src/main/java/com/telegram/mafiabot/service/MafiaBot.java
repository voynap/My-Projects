package com.telegram.mafiabot.service;

import com.telegram.mafiabot.model.Role;
import com.telegram.mafiabot.util.TelegramSticker;
import com.telegram.mafiabot.util.TelegramStickersFields;
import com.telegram.mafiabot.config.BotConfig;
import com.telegram.mafiabot.model.Player;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.stickers.GetStickerSet;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.api.objects.stickers.StickerSet;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.telegram.mafiabot.util.*;

import java.util.*;

import static com.telegram.mafiabot.util.Words.*;


@Component
@Slf4j
public class MafiaBot extends TelegramLongPollingBot {

    List<Player> signedPlayers = new ArrayList<>();
    private final Map<String, Game> gamesMap = new HashMap<>();
    private final PlayersService playersService;

    final BotConfig config;
    static final String HELP_TEXT = "Смотри, что могу : \n" +
            "/join - присоединиться к игре\n" +
            "/sign_up - записаться на ближайшую игру 2\n" +
            "Test line 3";

    @Autowired
    public MafiaBot(BotConfig config, PlayersService playersService) {
        this.config = config;
        this.playersService = playersService;
    }


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Присоединение игрока
            if(gamesMap.containsKey(update.getMessage().getText())) {
                Game game = gamesMap.get(update.getMessage().getText()); // Проверяем что игра не началась
                if (!game.isNumbersAssigned) {
                    playerJoined(update, chatId);
                } else {
                    sendMessage(chatId, "Ой, кажется игра уже началась.");
                }
                           }

            //TODO - Добавить Location функционал, ManyToOne

            if (messageText.matches("^([1-9]|1[0-5])$")) {
                digitsProcess(chatId, messageText);
            }

            switch (messageText) {
                case "/start"           ->  onStart(chatId, update.getMessage().getChat().getFirstName(), update.getMessage());
                case "/master"          -> createSession(update, chatId);
                case "/help"            -> showHelp(chatId);
                case "/join", "/gamer"  -> joinGame(chatId);
                case "/add_guest"       -> addGuest(chatId);
                case "/add_guest8"      -> addGuest8(chatId);
                case "/delete_guest"    -> deleteGuest(chatId);
                case "/sign_up"         -> signUp(chatId);
                case "/start_game"      -> startGame(chatId);
                case "/players"         -> getGamePlayers(chatId);
                case "/numbers"          -> getPlayersAndNumbers(chatId);
                case "/next"            -> nextPhase(chatId);
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            String[] dataArray = data.split(" ");

            if (dataArray.length == 3 && dataArray[1] != null) {
                // TODO - вынести в отдельный метод
                String roleName = dataArray[0];
                String gameKey = dataArray[1];
                String username = dataArray[2];
                Game game = gamesMap.get(gameKey);

                Player player = game.getPlayers().stream().filter(p -> p.getUsername().equals(username)).findFirst().get();
                player.setRole(Role.valueOf(roleName.split("_")[0]));
                sendMessage(game.getGameMasterChatId(), "Игроку " + player.getUsername() + " назначена роль " + player.getRole().toString());
//                if (chatId != game.getGameMasterChatId()) {
//                    confirm(chatId, player.getRole());
//                } else {
//                    sendMessage(chatId, "Вы игрок и ведущий одновременно? Серьезно? ");
//                }
                // Find the guest player by username and assign the selected role
                for (Player guest : gamesMap.get(gameKey).getPlayers()) {
                    if (guest.getName().equals("Guest") && guest.getUsername().equals(username)) {
                        guest.setRole(Role.valueOf(roleName.split("_")[0]));
                        break;
                    }
                }
            }

            switch (callbackData) {
                case "YES_BUTTON"       -> yesButtonProcess(chatId, messageId);
                case "NO_BUTTON"        -> noButtonProcess(chatId);
                case "SIGN_UP_BUTTON"   -> signUpButtonProcess(chatId, messageId);
                case "CANCEL_BUTTON"    -> cancelButtonProcess(chatId);
                case "CONTINUE_BUTTON"  -> continueButtonProcess(chatId);
                case "BEGIN_BUTTON"     -> beginButtonProcess(chatId);
                case "CONFIRM_ROLE_BUTTON" -> confirmButtonProcess(chatId);
                case "PASS_BUTTON" -> passProcess(chatId);
                case "NIGHT_BUTTON" -> nightProcess(chatId);
                case "GAME_BUTTON" -> startGameButton(chatId);
            }


        }
    }

    private void startGameButton(long chatId) {
        Game game = findGameByMasterChatId(chatId);
        game.beginGame();
    }

    private void nightProcess(long chatId) {
        Game game = findGameByMasterChatId(chatId);
        game.setNightFlag(true);
        game.setPhase(5);
    }

    private void passProcess(long chatId) {
        Game game = findGameByMasterChatId(chatId);
        game.setPassFlag(true);
    }

    private void nextPhase(long chatId) {
        Player player = playersService.findById(chatId).get();
        if (player.isMaster()) {
            Game game = findGameByMasterChatId(chatId);
            if (!game.isNightFlag()) {
                game.setPhase(game.getPhase() + 1);
                if (game.getPhase() > 5) {
                    game.setPhase(0);
                }
            } else {
                game.setNightPhase(game.getNightPhase() + 1);
                if (game.getNightPhase() > 4) {
                    // TODO
                    game.setNightFlag(false);
                    game.setNightPhase(0);

                }
            }
        }
    }

    private void getPlayersAndNumbers(long chatId) {
        Game game = findGameByMasterChatId(chatId);
        game.checkPlayersAndNumbers();
    }

    private void addGuest8(long chatId) {
        for (int i = 0; i < 8; i++) {
            Game game = findGameByMasterChatId(chatId);
            Player guest = new Player();
            guest.setName("Guest");
            guest.setGameKey(game.getKey());
            game.getPlayers().add(guest);
            guest.setUsername("Guest" + game.getPlayers().size());
            String text = "Новый игрок был добавлен в качестве гостя. Текущее количество игроков : " + game.getPlayers().size();
            sendMessage(game.getGameMasterChatId(), text);
        }

    }

    private void confirmButtonProcess(long chatId) {
        sendMessage(chatId, "Вы подтвердили свою роль");
    }

    private void confirm(long chatId, Role role) {
        String text = null;
        switch (role) {
            case DON -> text = "Ваша роль ДОН. Подтвердить?";
            case MAFIA -> text = "Ваша роль МАФИЯ. Подтвердить?";
            case MANIAC -> text = "Ваша роль МАНЬЯК. Подтвердить?";
            case DOCTOR -> text = "Ваша роль ДОКТОР. Подтвердить?";
            case CHEF -> text = "Ваша роль ШЕРИФ. Подтвердить?";
            case KAMIKAZE -> text = "Ваша роль КАМИКАДЗЕ. Подтвердить?";
            case BOMB ->  text = "Ваша роль БОМБА. Подтвердить?";
            case CITIZEN -> text = "Ваша роль МИРНЫЙ ЖИТЕЛЬ. Подтвердить?";
        }

        SendMessage message = new SendMessage();
        oneButtonInit(message, chatId, text, "Подтвердить", "CONFIRM_ROLE_BUTTON");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }

    private void beginButtonProcess(long chatId) {
        Game game = findGameByMasterChatId(chatId);
        Thread thread = new Thread() {
            @Override
            public void run() {
                sendMessage(game.getGameMasterChatId(), "Запускаю таймер в 60 секунд.");
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sendMessage(game.getGameMasterChatId(), "Минута общего обсуждения завершена. Индивидуальное время.");
                sendMessage(game.getGameMasterChatId(), "Каждому игроку будет выделено 30 секунд. Используйте команду /next чтобы начать индивидуальное время");
                sendMessage(game.getGameMasterChatId(), "К сожалению я пока не умею делать тайм-ауты, поэтому дождитесь " +
                        "окончания индивидуального времени игроков, если кто-нибудь попросит тайм-аут");
            }
        };
        thread.start();


    }

    private void continueButtonProcess(long chatId) {
        Game game = findGameByMasterChatId(chatId);
        game.setNumbersAssigned(true);
        if (!game.isRolesAssigned) {
            for (Player player : game.getPlayers()) {
                if (!player.getName().equals("Guest")) {
                    SendMessage message = new SendMessage();
                    rolesButtonInit(message, player.getChatId(), "Пожалуйста, сообщите свою роль в игре", player);
                        try {
                             execute(message);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                    }
                }
            }
        } else {

        }
    }

    private void cancelButtonProcess(long chatId) {
        Player player = playersService.findById(chatId).get();
        signedPlayers.remove(player);
        sendMessage(chatId, "Вы отменили запись на игру :c ");
    }

    private void signUpButtonProcess(long chatId, long messageId) {
        Player player = playersService.findById(chatId).get();
        if (!signedPlayers.contains(player)) {
            signedPlayers.add(player);
            getSignUpMessage();
            executeEditMessageText("Вы были записаны на ближайшую игру", chatId, messageId);
            sleep(500);
            sendMessage(chatId, getSignUpMessage());
        } else {
            cancel(chatId);
        }
    }

    private void noButtonProcess(long chatId) {
        Game game = findGameByMasterChatId(chatId);
        sendMessage(chatId, "Текущее количество присоединившихся игроков : " + game.getPlayers().size() +"\n" +
                "Необходимый минимум для начала 8 игроков\n" +
                "Максимум игроков в одной сессии - 15\n" +
                "Добавьте игроков в качестве гостей, если необходимо");
    }

    private void yesButtonProcess(long chatId, long messageId) {
        Game game = findGameByMasterChatId(chatId);
        Player guest = new Player();
        guest.setName("Guest");
        guest.setGameKey(game.getKey());
        game.getPlayers().add(guest);
        guest.setUsername("Guest" + game.getPlayers().size());
        String text = "Новый игрок был добавлен в качестве гостя. Текущее количество игроков : " + game.getPlayers().size();
        executeEditMessageText(text, chatId, messageId);
    }


    private void getGamePlayers(long chatId) {

        Game game = findGameByMasterChatId(chatId);
        Player player = playersService.findById(chatId).get();
        if (player.isMaster() && game.getGameMasterChatId() == chatId) {
            sendMessage(chatId, game.generatePlayerAndNumbersMessage(game.isRolesAssigned));
        } else {
            sendMessage(chatId, game.generatePlayerAndNumbersMessage(false));
        }

    }


    private void startGame(long chatId) {
        try {
            Player master = playersService.findById(chatId).get();
            if (master.isMaster()) {
                Game game = findGameByMasterChatId(chatId);
                if (game == null) {
                    sendMessage(chatId, "Игру необходимо сперва создать. \nИспользуйте команду /master");
                    return;
                }
                sendMessage(chatId, "Ждем пока присоединится минимум 8 игроков");
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Game g = findGameByMasterChatId(chatId);
                        if (g.isReady) {
                            sendMessage(game.getGameMasterChatId(), "Похоже игра уже началась, и находится в самом разгаре");
                            return;
                        }
                        checkForPlayers(game);
                        sendMessage(chatId, "Минимальное количество игроков присоединилось, ждем пока все отправят мне свои номера.");
                        game.prepare();
                    }
                };
                thread.start();

            } else {
                sendMessage(chatId, "Извините, только ведущий может начинать игры");
            }
        } catch (NullPointerException e) {
            sendMessage(chatId, "Игру необходимо сперва создать. Используйте команду /master");
        }
    }

    private void checkForPlayers(Game game) {
        while (game.getPlayers().size() < 8) {
            // wait for 8 players to join
            this.sleep(1000);
        }
    }

    private void createSession(Update update, long chatId) {
        Player player = playersService.findById(chatId).get();
        if (player.isMaster()) {
            String key = generateKey();
            sendMessageWithHtml(chatId, "Ваш ключ-идентификатор для игры : " + "<b>" + key + "</b>"  + "\n" +
                    "Сообщите его другим игрокам, чтобы они смогли присоединиться к игре");

            Game game = new Game(this, key,chatId);
            gamesMap.put(key,game);
            if(game.getPlayers().size() == 15) {
                startGame(chatId);
            }
        } else {
            sendMessage(chatId, "Только ведущие могут создавать игры");
        }
    }


    private void digitsProcess(long chatId, String messageText) {
        Player player = playersService.findById(chatId).get();
        Game game = gamesMap.get(player.getGameKey());
        if (!game.isNumbersAssigned()) {
            if (!game.getPlayerMap().containsValue(player)) {
                player.setPlayNumber(Integer.parseInt(messageText));
                game.playerMap.put(Integer.parseInt(messageText), player);
                sendMessage(chatId, "Вам установлен номер " + messageText);
                sendMessage(game.getGameMasterChatId(), "Игроку " + player.getUsername() + " был установлен номер " + messageText);
            } else {
                int targetKey = Integer.parseInt(messageText);
                Player switchPlayer = game.playerMap.get(targetKey);
                switchPlayer.setPlayNumber(player.getPlayNumber());
                if (switchPlayer.getPlayNumber() == 0) {
                    switchPlayer.setPlayNumber(1);
                }
                player.setPlayNumber(Integer.parseInt(messageText));
                int currentPlayerKey = 0;
                for (HashMap.Entry<Integer, Player> entry : game.playerMap.entrySet()) {
                    if (entry.getValue().equals(player)) {
                        currentPlayerKey = entry.getKey();
                        break;
                    }
                }
                game.getPlayerMap().put(currentPlayerKey, switchPlayer);
                game.getPlayerMap().put(targetKey, player);
                sendMessage(chatId, "Вы изменили свой номер на " + messageText);
            }
        } else if (game.isNightFlag()) {
            if (player.isMaster() && player.getChatId() == game.getGameMasterChatId()) {
                if (game.getNightPhase() == 0) {
                    //МАФИЯ
                    if (messageText.equals("0")) {
                        sendMessage(game.getGameMasterChatId(),"Мафия стреляет мимо!");
                        return;
                    }
                    game.mafiaTurn(Integer.parseInt(messageText));
                    sendMessage(game.getGameMasterChatId(), "Мафия стреляет в игрока номер " + messageText);

                } else if (game.getNightPhase() == 1) {
                    //ДОН
                    game.donTurn(Integer.parseInt(messageText), chatId);


                } else if (game.getNightPhase() == 2) {
                    //МАНЬЯК
                    if (messageText.equals("0")) {
                        sendMessage(game.getGameMasterChatId(),"Маньяк остается дома этой ночью!");
                        return;
                    }
                    game.maniacTurn(Integer.parseInt(messageText));
                    sendMessage(game.getGameMasterChatId(), "Маньяк выбирает жертву - игрок номер " + messageText);

                } else if (game.getNightPhase() == 3) {
                    //ШЕРИФ
                    game.chefTurn(Integer.parseInt(messageText), chatId);


                } else if (game.getNightPhase() == 4) {
                    //ДОКТОР
                    game.doctorTurn(Integer.parseInt(messageText), chatId);


                }
            }
        } else if (game.getPhase() == 1) {
            Optional<Player> foundPlayer = game.playerMap.values()
                    .stream()
                    .filter(p -> p.getChatId() == chatId)
                    .findFirst();
            if (game.getIndividualTimePlayerNumber() == foundPlayer.get().getPlayNumber()) {
                game.addToVoteList(Integer.parseInt(messageText), chatId);
                sendMessage(chatId, "Вы выставили игрока номер " + messageText);
                sendMessage(game.getGameMasterChatId(), "Игрок " + foundPlayer.get().getName() + " выставляет игрока номер " + messageText);

            }
        } else if (game.getPhase() == 2) {
            if (chatId == game.getGameMasterChatId()) {
                game.addToVoteList(Integer.parseInt(messageText));
            }
        } else if (game.getPhase() == 3) {
            if (game.getGameMasterChatId() == chatId) {
                Player votedPlayer = game.getPlayerMap().get(Integer.parseInt(messageText));
                sendMessage(votedPlayer.getChatId(), "Вас посчитали мафией, вы покидаете игру. \n" +
                        "Спасибо за участие!");
                votedPlayer.setAlive(false);
                sendMessage(game.getGameMasterChatId(), "Игрок номер " + messageText + " покидает игру");
                game.getPlayerMap().remove(votedPlayer.getPlayNumber());
            }
        }
    }



    private void signUp(long chatId) {

        sendMessage(chatId, getSignUpMessage());
        sleep(1000);
        SendMessage message = new SendMessage();
        oneButtonInit(message, chatId, "Хотите записаться на ближайшую игру?", "Записаться", "SIGN_UP_BUTTON");
        try {
            execute(message);
        } catch (TelegramApiException e) {

        }

    }

    private void cancel(long chatId) {
        SendMessage message = new SendMessage();
        oneButtonInit(message, chatId,"Кажется, вы уже записаны. Хотите отменить запись?", "Отменить", "CANCEL_BUTTON" );
        try {
            execute(message);
        } catch (TelegramApiException e) {

        }
    }

    private void deleteGuest(long chatId) {
        Player player = playersService.findById(chatId).get();
        if (player.isMaster()) {
            Game game = findGameByMasterChatId(chatId);
            List<Player> players = game.getPlayers();
            boolean hasGuestPlayer = players.stream()
                    .anyMatch(p -> p.getName().equals("Guest"));

            if (hasGuestPlayer) {
                players.stream()
                    .filter(p -> p.getName().equals("Guest"))
                    .findFirst()
                    .ifPresent(players::remove);
                sendMessage(chatId, "Гость был исключен из партии.\n" +
                        " Текущее количество игроков : " + players.size());
            } else {
                sendMessage(chatId, "И че я должен сделать? Гости не добавлены, алло!");
            }
        }
    }


    private void playerJoined(Update update, long chatId) {
        Game game = gamesMap.get(update.getMessage().getText());
        long masterChatId = game.getGameMasterChatId();
        sendMessage(masterChatId, "Игрок " + update.getMessage().getChat().getUserName() + " присоединился");
        Player player = playersService.findByUsername(update.getMessage().getChat().getUserName()).get(0);
        player.setGameKey(update.getMessage().getText());
        game.getPlayers().add(player);
        playersService.save(player);
        sendMessage(chatId, "Вы присоединились к игре ведущего " + playersService.findById(masterChatId).get().getName());
        sleep(1000);
        sendMessage(chatId, "Хорошей игры!");
        sleep(1000);
        sendSticker(chatId, TelegramStickersFields.PEPE_GUN.getId());

    }

    private Game findGameByMasterChatId(long chatId) {
        return  gamesMap.values().stream()
                .filter(g -> g.getGameMasterChatId() == chatId)
                .findFirst()
                .orElse(null);
    }

    private void addGuest(long chatId) {
        if (playersService.findById(chatId).isPresent()) {
            Player player = playersService.findById(chatId).get();
            if (player.isMaster()) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Хотите добавить нового игрока в качестве гостя?");
                buttonsInit(message);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                sendMessage(chatId, "Извините, эта команда только для ведущих :(");
            }
        }
    }

    private void joinGame(long chatId) {
        sendMessage(chatId, "Готовы поиграть?\n Спросите у ведущего ключ игры, и отправьте мне");
    }



    private void onStart(long chatId, String firstName, Message message) {
        String answer = EmojiParser.parseToUnicode("Привет " + firstName + ", рад тебя видеть! " + ":grinning:");
        sendMessage(chatId, answer);
        sleep(1000);
        Random random = new Random();
        List<TelegramSticker> stickers = TelegramStickersFields.getHelloStickers();
        sendSticker(chatId, stickers.get(random.nextInt(stickers.size())).getId());
        if(playersService.findById(message.getChatId()).isEmpty()) {
            sayHello(chatId, message);
        } else {
            Player player = playersService.findById(chatId).get();
            if (player.isMaster()) {
                sendMessage(chatId, "Кто ты сегодня? /gamer или /master ?");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                InlineKeyboardButton masterButton = new InlineKeyboardButton();
                masterButton.setText("Ведущий");
                masterButton.setCallbackData("MASTER");
                InlineKeyboardButton gamerButton = new InlineKeyboardButton();
                gamerButton.setText("Игрок");
                gamerButton.setCallbackData("Gamer");
                rowInLine.add(masterButton);
                rowInLine.add(gamerButton);
                rowsInline.add(rowInLine);
                inlineKeyboardMarkup.setKeyboard(rowsInline);
                message.setReplyMarkup(inlineKeyboardMarkup);
            }
        }
    }


    public void show () {

        GetStickerSet getStickerSet = new GetStickerSet();
        getStickerSet.setName("RickAndMorty");
        try {
            StickerSet stickerSet = execute(getStickerSet);
            List<Sticker> stickers = stickerSet.getStickers();
            for (Sticker sticker : stickers) {
                System.out.println(sticker.getFileId() + " " + sticker.getEmoji());
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        }

    private void showHelp(long chatId) {
        show();
        // /join - присоединиться к игре
        // /reserve - записаться на ближайшую игру
        // /faq - общая информация, стоимость
        // /rules - правила игры
    }

    protected void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {

        }
    }

    private void sendMessageWithHtml(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        message.setParseMode(ParseMode.HTML);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void executeEditMessageText(String text, long chatId, long messageId){
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {

        }
    }

    private void sendSticker(long chatId, String stickerId) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(chatId);
        sendSticker.setSticker(new InputFile(stickerId));
        try {
            execute(sendSticker);
        } catch (TelegramApiException e) {

        }
    }

    private void sayHello(long chatId, Message message) {
        sleep(1500);
        sendMessage(chatId, "Сейчас мы быстренько тебя зарегистрируем...");
        Chat chat = message.getChat();
        Player player = new Player();
        player.setChatId(chatId);
        player.setName(chat.getFirstName());
        player.setUsername(chat.getUserName());
        player.setScore(0);
        playersService.save(player);
        sleep(2000);
        sendMessage(chatId, "Готово! Можем приступать к игре");
        sleep(1500);
        sendSticker(chatId, TelegramStickersFields.SHERLOCK_CLOWN_PEPE.getId());
    }

    protected void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private String generateKey() {
        Random random = new Random();
        String key = adjectives.get(random.nextInt(adjectives.size())) +
                     colors.get(random.nextInt(colors.size())) +
                     animals.get(random.nextInt(animals.size())) + "With" +
                     items.get(random.nextInt(items.size()));

        return key;
    }
    private void buttonsInit(SendMessage message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        yesButton.setCallbackData("YES_BUTTON");

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData("NO_BUTTON");

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInline.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);
    }

    protected void oneButtonInit(SendMessage message, long chatId, String messageText, String buttonText, String buttonName) {
        message.setText(messageText);
        message.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton signUpButton = new InlineKeyboardButton();
        signUpButton.setText(buttonText);
        signUpButton.setCallbackData(buttonName);
        rowInLine.add(signUpButton);
        rowsInline.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);
    }



    protected void rolesButtonInit(SendMessage message, long chatId, String text, Player player) {

        message.setText(text);
        message.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        InlineKeyboardButton donButton = new InlineKeyboardButton();
        donButton.setText("Дон");
        donButton.setCallbackData("DON_BUTTON" + " " +  player.getGameKey() + " " + player.getUsername());

        InlineKeyboardButton mafiaButton = new InlineKeyboardButton();
        mafiaButton.setText("Мафия");
        mafiaButton.setCallbackData("MAFIA_BUTTON" + " " +  player.getGameKey() + " " + player.getUsername());

        InlineKeyboardButton doctorButton = new InlineKeyboardButton();
        doctorButton.setText("Доктор");
        doctorButton.setCallbackData("DOCTOR_BUTTON" + " "  + player.getGameKey() + " " + player.getUsername());

        InlineKeyboardButton maniacButton = new InlineKeyboardButton();
        maniacButton.setText("Маньяк");
        maniacButton.setCallbackData("MANIAC_BUTTON" + " " + player.getGameKey() + " " + player.getUsername());

        row1.add(donButton);
        row1.add(mafiaButton);
        row1.add(doctorButton);
        row1.add(maniacButton);

        InlineKeyboardButton chefButton = new InlineKeyboardButton();
        chefButton.setText("Шеф");
        chefButton.setCallbackData("CHEF_BUTTON" + " " + player.getGameKey() + " " + player.getUsername());

        InlineKeyboardButton civilianButton = new InlineKeyboardButton();
        civilianButton.setText("Мирный житель");
        civilianButton.setCallbackData("CITIZEN_BUTTON" + " " + player.getGameKey() + " " + player.getUsername());

        InlineKeyboardButton kamikazeButton = new InlineKeyboardButton();
        kamikazeButton.setText("Камикадзе");
        kamikazeButton.setCallbackData("KAMIKAZE_BUTTON" + " " + player.getGameKey() + " " + player.getUsername());

        InlineKeyboardButton bombButton = new InlineKeyboardButton();
        bombButton.setText("Бомба");
        bombButton.setCallbackData("BOMB_BUTTON" + " " + player.getGameKey() + " " + player.getUsername());

        row2.add(chefButton);
        row2.add(civilianButton);
        row2.add(kamikazeButton);
        row2.add(bombButton);

        rowsInline.add(row1);
        rowsInline.add(row2);

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);

    }


    public String getSignUpMessage() {
        String signedUpMessage = new String(BotMessage.AMIROVKA_MESSAGE);
        StringBuilder builder = new StringBuilder(signedUpMessage);
        if (!signedPlayers.isEmpty()) {
            for (Player player : signedPlayers) {
                builder.append("\n");
                builder.append((signedPlayers.indexOf(player) + 1) + " - ");
                builder.append(player.getName());
            }
            return builder.toString();
        } else {
            return BotMessage.AMIROVKA_MESSAGE;
        }
    }


}
