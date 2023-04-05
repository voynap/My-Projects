package com.telegram.mafiabot.service;

import com.telegram.mafiabot.model.Player;
import com.telegram.mafiabot.model.Role;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

import static com.telegram.mafiabot.util.FunnyMessageGenerator.generateRandomMessage;


public class Game {

    private final MafiaBot mafiaBot;

    private List<Player> players = new ArrayList<>();

    protected Map<Integer, Player> playerMap = new TreeMap<>();

    private final String key;

    private int day = 1;
    
    protected int dayPhase; // переменная текущей фазы игры.

    protected int nightPhase;
    protected boolean nightFlag = false;

    private final long gameMasterChatId;
    public boolean isRolesAssigned = false;

    public boolean isNumbersAssigned = false;

    protected boolean passFlag = false;

    protected boolean isReady = false;

    protected int individualTimePlayerNumber; // переменная для контроля за выставлением игрока

    private final List<Player> onVotePlayers = new ArrayList<>();

    private boolean isGameFinished = false;




    public Game(MafiaBot mafiaBot, String key, long gameMasterChatId) {
        this.mafiaBot = mafiaBot;
        this.key = key;
        this.gameMasterChatId = gameMasterChatId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getKey() {
        return key;
    }

    public boolean isNightFlag() {
        return nightFlag;
    }

    public void setNightFlag(boolean nightFlag) {
        this.nightFlag = nightFlag;
    }

    public int getDayPhase() {
        return dayPhase;
    }

    public void setDayPhase(int dayPhase) {
        this.dayPhase = dayPhase;
    }

    public int getNightPhase() {
        return nightPhase;
    }

    public int getIndividualTimePlayerNumber() {
        return individualTimePlayerNumber;
    }

    public void setIndividualTimePlayerNumber(int individualTimePlayerNumber) {
        this.individualTimePlayerNumber = individualTimePlayerNumber;
    }

    public void setNightPhase(int nightPhase) {
        this.nightPhase = nightPhase;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public long getGameMasterChatId() {
        return gameMasterChatId;
    }

    public boolean isNumbersAssigned() {
        return isNumbersAssigned;
    }

    public void setNumbersAssigned(boolean numbersAssigned) {
        isNumbersAssigned = numbersAssigned;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Map<Integer, Player> getPlayerMap() {
        return playerMap;
    }



    public boolean isPassFlag() {
        return passFlag;
    }

    public void setPassFlag(boolean passFlag) {
        this.passFlag = passFlag;
    }

    public void setPlayerMap(Map<Integer, Player> playerMap) {
        this.playerMap = playerMap;
    }

    public int getPhase() {
        return dayPhase;
    }

    public boolean isRolesAssigned() {
        return isRolesAssigned;
    }

    public void setRolesAssigned(boolean rolesAssigned) {
        isRolesAssigned = rolesAssigned;
    }

    public void setPhase(int phase) {
        this.dayPhase = phase;
    }

    public void prepare() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                gameInitialization();
            }
        };

        thread.start();
    }


    private void gameInitialization() {
            for (Player player : players) {
                if (!player.getName().equals("Guest"))
                    mafiaBot.sendMessage(player.getChatId(), "Пожалуйста, отправь мне свой номер");
            }


            List<Player> nonGuestPlayers = players.stream()
                    .filter(player -> !player.getName().equals("Guest"))
                    .toList();

            while (!(playerMap.values().containsAll(nonGuestPlayers))) {
                mafiaBot.sleep(1000); // ?? check if works
            }
            setPlayNumbersForGuests();
            mafiaBot.sendMessage(gameMasterChatId, "Используйте команду /numbers чтобы проверить игроков и их игровые номера");

            System.out.println(playerMap.toString());
            while (!isNumbersAssigned) {
                // Ждем пока ведущий нажмет кнопку продолжить
                // Continue button - ask for players to send their roles to bot
                mafiaBot.sleep(1000);
            }

            while (nonGuestPlayers.stream().anyMatch(player -> player.getRole() == null)) {
                System.out.println("LOOP");
                mafiaBot.sleep(500);
            }


            mafiaBot.sendMessage(gameMasterChatId, "Игрокам назначены роли, необходимо назначить роли гостям если такие есть.");


        //Игрокам назначены роли.

        for (Player player : players) {
            if (player.getName().equals("Guest")) {
                if (player.getRole() == null) {
                    SendMessage message = new SendMessage();
                    mafiaBot.rolesButtonInit(message, gameMasterChatId, "Назначьте роль игроку " + player.getUsername(), player);
                    try {
                        mafiaBot.execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
            mafiaBot.sendMessage(gameMasterChatId, "Город уходит ко сну, господин ведущий.\n" +
                    "Пожалуйста, проверьте все роли игроков еще раз, и назначьте их гостям." );

        while (!isRolesAssigned) {
            mafiaBot.sleep(1000);
            if (players.stream().allMatch(p -> p.getRole() != null)) {
                setRolesAssigned(true);
            }
        }

        for (Player player : players) {
            System.out.println(player.getName() + player.getRole());
        }

        synchronizeRolesInMapAndList();

        mafiaBot.sendMessage(gameMasterChatId, "Вы можете в любой момент узнать игроков и их роли используя команду /players");

        SendMessage message = new SendMessage();
        mafiaBot.oneButtonInit(message, gameMasterChatId, "Все готово. Хотите начать игру?" , "Начать", "GAME_BUTTON");
        try {
            mafiaBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        while (!isReady) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        synchronizePlayersNumbers();

        mafiaBot.sendMessage(gameMasterChatId, "Все готово, игра начнется через 10 секунд");
        mafiaBot.sleep(10000);

            startGame();

        }

    private void synchronizePlayersNumbers() {
        for (Map.Entry<Integer,Player> playerEntry : playerMap.entrySet()) {
            Player player = playerEntry.getValue();
            player.setPlayNumber(playerEntry.getKey());
        }
    }

    private void synchronizeRolesInMapAndList() {
        for (Player mapPlayer : playerMap.values()) {
            for (Player listPlayer : players ) {
                if (mapPlayer.equals(listPlayer)) {
                    mapPlayer.setRole(listPlayer.getRole());
                    break;
                }
            }
        }
    }

    protected void checkPlayersAndNumbers() {
        String playersAndNumbersMessage = generatePlayerAndNumbersMessage(isRolesAssigned);
        mafiaBot.sendMessage(gameMasterChatId, playersAndNumbersMessage);
        SendMessage sendMessage = new SendMessage();
        mafiaBot.oneButtonInit(sendMessage, gameMasterChatId, "Проверьте, что все правильно", "Продолжить", "CONTINUE_BUTTON");
        mafiaBot.sendMessage(gameMasterChatId, "Используйте /numbers чтобы обновить список");
        try {
            mafiaBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }



    public void startGame() {

        /*
        Фазы игры :
        0 - Общее время
        1 - Индивидуальное время
        2 - Время выставленных игроков
        3 - Голосование
        4 - Время игрока покидающего игру
        5 - Ночь
        6 - Итоги ночи. Убитые игроки, их время.
         */


        Thread game = new Thread() {
            @Override
            public void run() {


                while (!isGameFinished) {

                    setPhase(0);
                    // Фаза 0 - общая минута
                    generalMinute(day);
                    // Фаза 1 - индивидуальное время
                    personalTime(day);
                    // Фаза 2 - выставление
                    if (day == 1) {
                        setPhase(5);
                    }
                    if (getPhase() == 2) {

                        nominationTime();
                        // Фаза 3 - голосование
                        votingTime();
                        // Фаза 4 - время уходящего игрока
                        gonePlayerTime();
                    }
                    // Проверка, не завершилась ли игра
                    String message = checkGameStatus();
                    if (isGameFinished) {
                        mafiaBot.sendMessage(gameMasterChatId, message);
                        return;
                    }

                    while (dayPhase != 5) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    nightPhase = 0;

                    mafiaTime();

                    donTime();

                    maniacTime();

                    chefTime();

                    doctorTime();

                    day++;

                    morningTime(day);

                    String ms = checkGameStatus();
                    if (isGameFinished) {
                        mafiaBot.sendMessage(gameMasterChatId, ms);
                        return;
                    }
                }
            }
        };

        game.start();


    }


    private void morningTime(int day) {

        List<Integer> killedPlayersNumbers = new ArrayList<>();
        for (Map.Entry<Integer, Player> player : playerMap.entrySet()) {
            if (!player.getValue().isAlive()) {
                killedPlayersNumbers.add(player.getKey());
            }
        }


        mafiaBot.sendMessage(gameMasterChatId, "Утро в городе. День " + (day) + " Количество жертв : " + killedPlayersNumbers.size()  );


        for (Integer number : killedPlayersNumbers) {
            mafiaBot.sendMessage(gameMasterChatId, generateRandomMessage(number));
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SendMessage msg = new SendMessage();
            mafiaBot.oneButtonInit(msg, gameMasterChatId, "Время игрока номер " + number,
                    "ПАС", "PASS_BUTTON");
            try {
                mafiaBot.execute(msg);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            passFlag = false;
            for (int i = 0; i < 30; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (passFlag) {
                    break;
                }
            }
            mafiaBot.sendMessage(gameMasterChatId, "Апплодисменты ушедшему игроку.");
            playerMap.remove(number);
        }

    }
    private void doctorTime() {
        mafiaBot.sendMessage(gameMasterChatId, "Cпит шериф. Просыпается доктор.");
        mafiaBot.sendMessage(gameMasterChatId, "Отправьте мне цифру игрока, которого лечит доктор. \n");
        mafiaBot.sendMessage(gameMasterChatId, "Используйте команду /next чтобы перейти к следующему ходу");


        while (nightFlag) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void chefTime() {
        mafiaBot.sendMessage(gameMasterChatId, "Маньяк засыпает. Просыпается шериф.");
        mafiaBot.sendMessage(gameMasterChatId, "Отправьте мне цифру игрока, которого проверяет шериф. \n");
        mafiaBot.sendMessage(gameMasterChatId, "Используйте команду /next чтобы перейти к следующему ходу");

        while (nightPhase !=4) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void maniacTime() {
        mafiaBot.sendMessage(gameMasterChatId, "Дон спит. Маньяк просыпается.");
        mafiaBot.sendMessage(gameMasterChatId, "Отправьте мне цифру выбранного маньяком игрока. \n");
        mafiaBot.sendMessage(gameMasterChatId, "Используйте команду /next чтобы перейти к следующему ходу");

        while (nightPhase !=3) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void donTime() {
        mafiaBot.sendMessage(gameMasterChatId, "Мафия засыпает. Дон проверяет игрока.");
        mafiaBot.sendMessage(gameMasterChatId, "Отправьте мне цифру выбранного игрока. \n");
        mafiaBot.sendMessage(gameMasterChatId, "Используйте команду /next чтобы перейти к следующему ходу");

        while (nightPhase !=2) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void mafiaTime() {

        mafiaBot.sendMessage(gameMasterChatId, "Все высказались. Город отправляется спать. Господин ведущий выбирает музыку.");
        SendMessage sendMessage = new SendMessage();
        mafiaBot.oneButtonInit(sendMessage, gameMasterChatId, "Начать ночь?", "zZzzZz", "NIGHT_BUTTON");
        try {
            mafiaBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        while (dayPhase !=5) {
            // wait
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        nightPhase = 0;

        while (!nightFlag) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        mafiaBot.sendMessage(gameMasterChatId, "Просыпается МАФИЯ вместе с Доном.");
        mafiaBot.sendMessage(gameMasterChatId, "Отправьте мне цифру выбранного мафией игрока. \n" +
                " Если никто не убит, или произошел сострел отправьте цифру 0");

        mafiaBot.sendMessage(gameMasterChatId, "Используйте команду /next чтобы перейти к следующему ходу");

        while (nightPhase != 1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void gonePlayerTime() {
        SendMessage sendMessage = new SendMessage();
        mafiaBot.oneButtonInit(sendMessage, gameMasterChatId, "Время игрока покидающего игру по результатам голосования",
                "ПАС", "PASS_BUTTON");
        try {
            mafiaBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        passFlag = false;
        for (int j = 0; j < 30; j++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (passFlag) {
                break;
            }
        }

        setPhase(getPhase() + 1);
    }

    private void votingTime() {
        StringBuilder b = new StringBuilder("Выставлены следующие игроки : \n");
        for (Player player : onVotePlayers) {
            b.append("\n Игрок номер : ").append(player.getPlayNumber()).append(" ").append(player.getName());
        }

        mafiaBot.sendMessage(gameMasterChatId, b.toString());

        for (Player player : onVotePlayers) {
            SendMessage sendMessage = new SendMessage();
            mafiaBot.oneButtonInit(sendMessage, gameMasterChatId, "Время игрока номер " + player.getPlayNumber() + ", " + player.getUsername(),
                    "ПАС", "PASS_BUTTON");
            try {
                mafiaBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            passFlag = false;
            for (int j = 0; j < 30; j++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (passFlag) {
                    break;
                }
            }
        }

        StringBuilder builder = new StringBuilder("!!! ГОЛОСОВАНИЕ !!! \n" +
                "Выставлены игроки : ");
        for (Player player : onVotePlayers) {
            builder.append("\n Игрок номер : ").append(player.getPlayNumber()).append(" ").append(player.getName());
        }
        onVotePlayers.clear();
        String voteMessage = builder.toString();
        mafiaBot.sendMessage(gameMasterChatId, voteMessage);
        mafiaBot.sendMessage(gameMasterChatId, "Проведите голосование, господин ведущий, и " +
                "отправьте мне номер игрока, которого город посчитал мафией. \n \n " +
                "Если у нас равное количество голосов, предоставьте маньяку решить участь игроков, а затем отправьте мне номер. \n" +
                "Используйте команду /next чтобы перейти к следующей фазе");


        while (getPhase() != 4) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    private void nominationTime() {
        mafiaBot.sendMessage(gameMasterChatId, "Выставление РАЗ, ДВА. Кого назвали игроки, господин ведущий?" +
                "\n Отправьте мне номера, затем используйте команду /next");

        while (getPhase() !=3) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void personalTime(int day) {
        List<Player> order = new ArrayList<>();

        for (Map.Entry<Integer, Player> playerEntry : playerMap.entrySet()) {
            order.add(playerEntry.getValue());
        }

        if (day > 1) {
            Collections.rotate(order, -(day - 1));
        }

        for (Player player : order) {
            setIndividualTimePlayerNumber(player.getPlayNumber());
            SendMessage sendMessage = new SendMessage();
            mafiaBot.oneButtonInit(sendMessage, gameMasterChatId, "Время игрока номер " + player.getPlayNumber() + ", " + player.getUsername(),
                    "ПАС", "PASS_BUTTON");
            if (day > 1) {
                mafiaBot.sendMessage(player.getChatId(), "Если хотите выставить игрока, отправьте мне его номер, пока ваше время не кончилось");
            }
            try {
                mafiaBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            passFlag = false;
            for (int j = 0; j < 30; j++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (passFlag) {
                    break;
                }
            }

            mafiaBot.sendMessage(gameMasterChatId, "Спасибо, игрок номер " + player.getPlayNumber());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



        if (day != 1) {
            mafiaBot.sendMessage(gameMasterChatId, "Индивидуальное время игроков кончилось" +
                    "\n Используйте /next чтобы перейти к выставлению");
        } else {
            mafiaBot.sendMessage(gameMasterChatId, "Индивидуальное время игроков кончилось" +
                    "\n В игре первый день. Используйте /next чтобы перейти в ночь");
        }

        setIndividualTimePlayerNumber(-1);

        while (getPhase() != 2) {
            // wait /next
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void generalMinute(int day) {

        if (day == 1) {
            mafiaBot.sendMessage(gameMasterChatId, "Утро в городе. День номер " + day + ". Минута общего обсуждения у игроков.");
        } else {
            mafiaBot.sendMessage(gameMasterChatId, "Минута общего обсуждения");
        }

        SendMessage message = new SendMessage();
        mafiaBot.oneButtonInit(message, gameMasterChatId,
                "Хотите запустить таймер в одну минуту?", "Запустить", "BEGIN_BUTTON");
        try {
            mafiaBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        mafiaBot.sendMessage(gameMasterChatId, "Если вы мне не доверяете, вы так же можете использовать " +
                "команду /next для перехода к следующей фазе игры");

        while (dayPhase != 1) { // Фаза 1 - индивидуальное время
            //wait
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String checkGameStatus() {
        int blackCount = 0;
        int redCount = 0;
        int maniacCount = 0;

        List<Player> activePlayers = new ArrayList<>();

        for (Map.Entry<Integer, Player> playerEntry : getPlayerMap().entrySet()) {
            activePlayers.add(playerEntry.getValue());
        }

        for (Player player : activePlayers) {
            Role role = player.getRole();
            if (role.equals(Role.CITIZEN) || role.equals(Role.CHEF) || role.equals(Role.DOCTOR) ||
                    role.equals(Role.KAMIKAZE) || role.equals(Role.BOMB) || role.equals(Role.MANIAC)) {
                redCount++;
                if (role.equals(Role.MANIAC)) {
                    maniacCount++;
                }
            } else {
                blackCount++;
            }
        }

        if (activePlayers.size() == 3) {
            curfew();
        }
        if (isGameFinished)
            return null;

        if (maniacCount == 1 && ((blackCount == 1 && redCount == 0) || (redCount == 2  && blackCount == 0))) {
            isGameFinished = true;
            return "Победа маньяка!";

        }
          else if (blackCount >= redCount) {
             isGameFinished = true;
            return "Победа мафии!";
        } else if (blackCount == 0 && maniacCount == 0) {
            isGameFinished = true;
            return "Победа мирных!";
        }


          return "Игра продолжается";
    }

    private void curfew() {
        mafiaBot.sendMessage(gameMasterChatId, "!!! В городе комендантский час !!!");
        generalMinute(day);
        personalTime(day);
        nominationTime();
        votingTime();
        gonePlayerTime();
        String text = checkGameStatus();
        sendMessageToAll(text);
        mafiaBot.sendMessage(gameMasterChatId, text);
        isGameFinished = true;


    }

    public String generatePlayerAndNumbersMessage(boolean isRolesSet) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Player> player : playerMap.entrySet()) {
            builder
                    .append("Игрок ")
                    .append(player.getKey())
                    .append(" : ")
                    .append(player.getValue().getName());
            if (isRolesSet) {
                builder.append(" -> ").append(player.getValue().getRole());
            }
                builder.append("\n");
        }
        return builder.toString();
    }


    private void setPlayNumbersForGuests() {
        for (Player player : players) {
            if (player.getName().equals("Guest")) {
                for (int i = 1; i <=15 ; i++) {
                    if (!playerMap.containsKey(i)) {
                        player.setUsername("Player" + i);
                        player.setPlayNumber(i);
                        playerMap.put(i, player);
                        break;
                    }
                }
            }
        }
    }

    public void sendMessageToAll(String text) {
        for (Player player : players) {
            mafiaBot.sendMessage(player.getChatId(), text);
        }
    }

    private Player getByPlayNumber(int number) {
        for (Map.Entry<Integer, Player> playerEntry : playerMap.entrySet()) {
            if (playerEntry.getKey() == number) {
                return playerEntry.getValue();
            }
        }
        return null;
    }

    public void mafiaTurn(int number) {
        Player player = getByPlayNumber(number);
        kill(player);
    }

    private void kill(Player player) {
        player.setAlive(false);
    }


    public void donTurn(int number, long chatId) {
        Player player = getByPlayNumber(number);
        mafiaBot.sendMessage(chatId, "Дон проверяет игрока " + player.getName() + ". Его роль - " + player.getRole().toString() );
    }

    public void maniacTurn(int number) {
        Player player = getByPlayNumber(number);
        kill(player);
    }

    public void chefTurn(int number, long chatId) {
        Player player = getByPlayNumber(number);
        mafiaBot.sendMessage(chatId, "Шериф проверяет игрока " + player.getName() + ". Его роль - " + player.getRole().toString() );

    }

    public void doctorTurn(int number, long chatId) {
        Player player = getByPlayNumber(number);
        if (player.isAlive()) {
            mafiaBot.sendMessage(chatId, "Доктор проводит профилактический осмотр игрока " + player.getName());
        } else {
            player.setAlive(true);
            mafiaBot.sendMessage(chatId, "Доктор возвращает к жизни игрока " + player.getName() + ". Апплодисменты!");
        }
    }

    public void addToVoteList(int number, long chatId) {
        Player player = playerMap.get(number);
        if (player != null) {
            onVotePlayers.add(player);
            mafiaBot.sendMessage(gameMasterChatId, "Выставлен игрок номер " + player.getPlayNumber());
            mafiaBot.sendMessage(chatId, "Выставлен игрок номер " + player.getPlayNumber());
            mafiaBot.sendMessage(player.getChatId(), "Вас выставил на голосование игрок номер " + getIndividualTimePlayerNumber());
        }
    }

    public void addToVoteList(int number) {
        Player player = playerMap.get(number);
        if (player != null) {
            onVotePlayers.add(player);
            mafiaBot.sendMessage(gameMasterChatId, "Выставлен игрок номер " + player.getPlayNumber());

        }
    }

    public void beginGame() {
        setReady(true);
    }
}
