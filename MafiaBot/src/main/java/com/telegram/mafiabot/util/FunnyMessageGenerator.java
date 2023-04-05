package com.telegram.mafiabot.util;

import java.util.*;

public class FunnyMessageGenerator {

    private static final List<String> EVIDENCES = Arrays.asList(
            "перчатки", "печенье", "флакон яда", "топор", "чихуа-хуа", "окровавленный нож",
            "сломанный зонтик", "недоеденный гамбургер", "игрушечный пистолет", "недопитый чай", "парик", "розовые наручники"
    );

    private static final List<String> MURDER_METHODS = Arrays.asList(
            "сбит проезжавшим мимо автобусом", "задавлен каретой", "похищен НЛО",
            "задавлен бегемотом", "утоплен в бассейне с лимонадом", "съеден армией муравьёв", "сбит шайбой на хоккейном матче", "упал с моста на батут", "раздавлен роялем"
    );


    // THANKS GPT-4 <3
    private static final List<String> FULL_MESSAGES = Arrays.asList(
            "был найден мертвым, похоже на самый обычный случай нежелания жить. На месте преступления оставили книгу \"Справочник для начинающих мафиози\".",
            "мертв. На месте преступления обнаружен тотем, как гласит древняя мафиозная магия, теперь его дух будет мстить.",
            "стал жертвой смертельной моды на безопасные игры. Обнаружены следы излишнего энтузиазма и одежда, явно не подходящая для криминального мира.",
            "нашли мертвым с головой, засунутой в аквариум с пираньями. Кто бы мог подумать, что они такие кровожадные?",
            "обнаружили безжизненным, прижимая к груди бутылку кетчупа. Видимо, покушался на мафиози, которые обожают гамбургеры.",
            "умер от несчастного случая, валяясь в бассейне с крокодилами. Видимо, его слишком увлекла игра в мафию.",
            "оказался мертвым в своей кровати, укутанным в мафиозное одеяло. Видимо, в его снах было что-то не так...",
            "нашли мертвым в коробке с огромными плюшевыми медведями. Какая ирония судьбы.",
            "был жестоко убит во время просмотра страшного фильма про мафию. Видимо, он недооценил своих противников.",
            "нашли мертвым с шариками для пейнтбола во рту. Видимо, мафиози решили разнообразить свои методы.",
            "умер от травм, полученных при попытке перепрыгнуть через забор мафиозного особняка. Неудачная попытка.",
            "был найден убитым, с прищепками на ушах и носу. Видимо, кто-то решил научить его не слушать и не нюхать мафиозные разговоры.",
            "обнаружен безжизненным, завернутым в мафиозный таблоид. Видимо, он узнал слишком много.",
            "нашли мертвым с микрофоном и записывающим устройством. Видимо, он решил стать журналистом в мире мафии.",
            "был найден мертвым, прятавшись в бочке с ядом. Никто не знает, зачем он туда полез.",
            "обнаружен безжизненным, обматывавшим себя тонной бумаги. Видимо, он решил устроить бюрократический переворот.",
            "стал жертвой невероятно удачной стрельбы из мафиозной пушки. Никто не знает, как это могло произойти.",
            "нашли мертвым, глотавшим таблетки от умопомешательства. Видимо, мафиози распространяют их не только среди своих жертв.",
            "обнаружен безжизненным, сидя на месте водителя в мафиозной машине. Видимо, он попытался угнать ее у настоящих мафиози.",
            "умер от переедания пиццы. Видимо, он не смог устоять перед мафиозными традициями.",
            "был найден мертвым, прятавшимся под мафиозным столом. Видимо, он решил устроиться на работу к мафиози.",
            "стал жертвой коварного удара мафиозного ботинка. Никто не знает, кто стоит за этим нападением.",
            "обнаружили безжизненным, лежащим в мафиозном солярии. Видимо, он решил немного отдохнуть перед игрой в мафию.",
            "нашли мертвым, облизывавшим мафиозный нож. Видимо, он решил проверить, насколько он острый.",
            "умер от ушиба головного мозга, полученного во время игры в мафию. Никто не знает, как это могло случиться.",
            "обнаружен безжизненным, замерзшим в мафиозном холодильнике. Видимо, он решил спрятаться от мафиози, но не учел последствий.",
            "обнаружен мертвым, закопанным в горе пиццы. Видимо, мафиози решили его похоронить в своей любимой еде.",
            "нашли мертвым с кучей карточек для игры в мафию во рту. Вот это азарт!",
            "нашли убитым с вилкой в глазу, после того как пытался поймать мафию на обеде. Остроумно!",
            "умер, случайно споткнувшись о свою коллекцию мафиозных кукол. Вот такая вот неожиданная судьба.",
            "обнаружен мертвым, связанным мафиозными галстуками. Оригинальный выбор мафии!",
            "умер, задушенный подушкой с мафиозными эмблемами. Никто не мог подумать, что подушка станет такой опасной.",
            "нашли мертвым, вцепившись в свой смартфон с открытой страницей «Как распознать мафию». Видимо, опоздал.",
            "обнаружен убитым, сидя на стуле с электрическими проводами, подключенными к нему. Видимо, мафиози устали от его разговоров.",
            "нашли мертвым, окруженным пустыми банками с газировкой. Никто не мог подумать, что мафиози устроят такую вечеринку.",
            "умер от травм, полученных при попытке спастись от мафии на велосипеде. Видимо, он недооценил их способности.",
            "нашли мертвым, прижавшись к телевизору. Видимо, хотел узнать победителей телешоу слишком близко.",
            "умер от переедания конфет. Кто бы мог подумать, что сладкое так сильно затянет?",
            "обнаружен мертвым в бассейне с лимонадом. Видимо, пытался поплавать среди сладостей.",
            "нашли мертвым, утопившимся в горе плюшевых мишек. Вот такой неожиданный образец нежности.",
            "умер, случайно выпив алкогольный напиток из коллекции своего дедушки. Видимо, она оказалась слишком крепкой.",
            "нашли мертвым, после попытки прыгать с парашютом, используя зонт. Не самый удачный выбор.",
            "обнаружен мертвым, задушенный горой своих собственных носков. Вот это да, стильная смерть!",
            "умер от травм, полученных при падении с карусели. Видимо, развлечения стали слишком опасными.",
            "нашли мертвым, закопанным в песке на пляже. Видимо, забыли сказать, что это не настоящее захоронение.",
            "умер, попытавшись решить кубик Рубика с завязанными глазами и ногами. Интересный подход!",
            "нашли мертвым, утопленным в аквариуме. Видимо, пытался познакомиться с рыбками слишком близко.",
            "обнаружен мертвым, завернутым в множество газет. Видимо, хотел быть в курсе всех новостей.",
            "умер от удара гитарой. Видимо, рок-н-ролл в его сердце был слишком сильным.",
            "нашли мертвым, с растяжкой на шее, изготовленной из своих собственных галстуков. Неожиданный финал.",
            "обнаружен мертвым, окруженный коллекцией разноцветных бабочек. Красочный финал.",
            "нашли мертвым, сидя на качелях. Похоже, вечерняя прогулка закончилась неудачно.",
            "умер после попытки установить новый рекорд на скутере. Скорость, однако, была слишком велика.",
            "нашли мертвым, покрытым огромным количеством пузырей от мыльной воды. Мыльный рецепт оказался смертоносным.",
            "обнаружен мертвым, обернутым в километры проволоки. Очень странный выбор декораций.",
            "умер от укуса своей собственной домашней хамелеон-змеи. Внезапный предатель.",
            "нашли мертвым, заваленным горами книг. Похоже, чтение стало смертельным увлечением.",
            "обнаружен мертвым, валяясь на полу с мороженым в руках. Калории, однако, дали о себе знать.",
            "умер после попытки соревноваться с машиным в шахматы. Искусственный интеллект был слишком сильным.",
            "нашли мертвым, разбросанным пазлами по всей комнате. Похоже, картинка не складывалась.",
            "обнаружен мертвым, окруженный котами. Милые создания, однако, оказались смертоносными.",
            "умер после попытки пройти через невидимую стену. Что за странный фокус!",
            "нашли мертвым, утопленным в бассейне с картофельными чипсами. Вкусная смерть.",
            "обнаружен мертвым, забыт на карнавальной гондоле. Праздник стал смертельным.",
            "умер после попытки прыгнуть через пламя. Огонь, однако, оказался слишком высоким.",
            "обнаружен мертвым после сеанса экстрасенса. Оказывается, заглянув в свое будущее, он увидел свою смерть.",
            "умер, пытаясь разгадать тайну Бермудского треугольника. В итоге стал его частью.",
            "нашли безжизненным после попытки научиться летать, используя только зонтик. Ветер носит не только листья.",
            "обнаружен мертвым после того, как случайно выпил зелье невидимости. К сожалению, его нашли слишком поздно.",
            "нашли мертвым после того, как случайно забрел в мафиозный флешмоб. Для одного участника шоу стало слишком реалистичным.",
            "умер после неудачного участия в мафиозном телешоу. Вот такой неожиданный поворот событий.",
            "обнаружен мертвым после встречи с мафией на костюмированной вечеринке. Похоже, костюм полицейского был не лучшим выбором.",
            "умер после случайного участия в мафиозном соревновании по пряткам. К сожалению, его никто не нашел вовремя.",
            "нашли мертвым после попытки сыграть на мафиозной свадьбе. Невеста явно не оценила его музыкальный талант.",
            "обнаружен безжизненным после посещения мафиозного фитнес-клуба. Тяжелоатлетические тренировки иногда опасны.",
            "умер после попытки попасть на мафиозное телевидение. Оказывается, камера добавляет не только килограммы, но и опасность.",
            "нашли мертвым после участия в мафиозной версии шоу «МастерШеф». Ядовитые ингредиенты оказались смертельными.",
            "обнаружен мертвым после попытки пробежаться по территории мафиозного особняка. Охранники были недовольны.",
            "умер в результате участия в мафиозном конкурсе красоты. К сожалению, его обаяние не спасло его от опасности.",
            "был обнаружен мертвым, закручивая сигару, которая явно была слишком крепкой для него. Мафиози считают это попыткой унизить их крепкие нервы.",
            "нашли его безжизненным, сидящим в ванной, заполненной водой и конфетти. Никто не понял, зачем он решил устроить вечеринку с мафиози.",
            "обнаружили мертвым, прятавшимся в шкафу с толстовками с надписью 'I love Mafia'. Как же он был предан своим идеалам!",
            "был найден мертвым, лежащим на кровати, облитой красной краской. Видимо, он хотел подчеркнуть свою любовь к мафиозному стилю.",
            "нашли мертвым, облаченным в костюм и лежащим на груде банкнот. Похоже, он слишком долго считал свои доходы и обидел мафиози.",
            "стал жертвой необычной смерти, падая с крыши в бассейн, наполненный шампанским. Похоже, он хотел выразить свою любовь к мафиозному стилю жизни.",
            "обнаружен мертвым, сидя на стуле с восковой фигурой в руках. Никто не понял, зачем он решил сделать восковую копию своих мафиозных друзей.",
            "был найден мертвым, лежащим на полу, окруженным кучей карт, фишек и кубиков. Похоже, он проиграл в последней партии и не смог пережить поражение.",
            "нашли его мертвым, сидящим в кресле, заполненном мафиозными газетами. Видимо, он не смог вынести новостей о том, что его любимый мафиозный босс был арестован.",
            "обнаружен мертвым, висящим на крючке в мясной лавке. Никто не знает, как он туда попал, но все заметили, что мафиози сильно заржали, увидев это зрелище."


            );

    private static final Random RANDOM = new Random();

    public static String generateRandomMessage(int playerNumber) {
        String message;

        // Select a random template
        int template = RANDOM.nextInt(10);

        if (template == 0) {
            // Template 1: Evidence found
            String evidence1 = EVIDENCES.get(RANDOM.nextInt(EVIDENCES.size()));
            String evidence2 = EVIDENCES.get(RANDOM.nextInt(EVIDENCES.size()));
            String evidence3 = EVIDENCES.get(RANDOM.nextInt(EVIDENCES.size()));
            message = "Игрок номер " + playerNumber + " был убит этой ночью. На месте преступления найдены :  " + evidence1 + ", " + evidence2 + ", " + evidence3 + ".";
        } else if (template == 1) {
            // Template 2: Murder method
            String murderMethod = MURDER_METHODS.get(RANDOM.nextInt(MURDER_METHODS.size()));
            message = "Игрок номер " + playerNumber + " был " + murderMethod + " этой ночью.";
        } else {
            // FULL MESSAGES
            String fullmessage = FULL_MESSAGES.get(RANDOM.nextInt(FULL_MESSAGES.size()));
            message = "Игрок номер " + playerNumber + " " + fullmessage;


        }

        return message;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(generateRandomMessage(i + 1));
        }
    }
}
