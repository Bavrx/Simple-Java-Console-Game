import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.random.RandomGenerator;

// Spiel Start
public class Main {
    public static void main() throws InterruptedException {
        Game game = new Game();
        game.play();
    }
}

// Terminal User Interface
class TUI{
    public void playerSelect(){
        System.out.println
                (
                        "=== Player ===" +
                                "\n(1) Warrior (HP 550 | AD 30)" +
                                "\n(2) Mage (HP 200 | AD 60)" +
                                "\n(3) Archer (HP 350 | AD 20)\n" +
                                "=== SELECT ==="
                );
    }

    public void playerFight(Player player, Enemy enemy){
        System.out.println
                (
                        "\n======== KAMPF ========" +
                                "\nPlayer: " + player.name +
                                "\nLVL: " + player.level +
                                "\nHP: " + player.health + "/" + player.maxHealth +
                                "\nCoins: " + player.coins +
                                "\nEP: " + player.exp + "/" + player.expToNextLevel +
                                "\n-----------------------" +
                                "\n\n\n-----------------------" +
                                "\nGegner: " + enemy.name +
                                "\nHP: " + enemy.health + "/" + enemy.maxHealth +
                                "\n=======================" +
                                "\n\n(1) ANGREIFEN\n" + "(2) HEILEN\n" + "(3) INVENTAR\n" + "(4) FLIEHEN\n"
                );
    }

    public void playerFightBoss(Player player, Boss boss){
        System.out.println
                (
                        "\n======== KAMPF ========" +
                                "\nPlayer: " + player.name +
                                "\nLVL: " + player.level +
                                "\nHP: " + player.health + "/" + player.maxHealth +
                                "\nCoins: " + player.coins +
                                "\nEP: " + player.exp + "/" + player.expToNextLevel +
                                "\n-----------------------" +
                                "\n\n\n-----------------------" +
                                "\nGegner: " + boss.name +
                                "\nHP: " + boss.health + "/" + boss.maxHealth +
                                "\n=======================" +
                                "\n\n(1) ANGREIFEN\n" + "(2) HEILEN\n" + "(3) INVENTAR\n" + "(4) FLIEHEN\n"
                );
    }

    public void spawnShop(){
        System.out.println("Du hast einen Shop gefunden!");
        System.out.println("Reingehen?");
        System.out.println("(1) Ja");
        System.out.println("(2) Nein");
    }
}

class Entity{
    String name;
    int health;
    int maxHealth;
    int attackDamage;
    int coins;
    int exp;
    int level;
    boolean alive;
    int expToNextLevel = 100;

    public Entity(String name, int health, int maxHealth, int attackDamage, int coins, int exp, boolean alive, int level) {
        this.name = name;
        this.health = health;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.coins = coins;
        this.exp = exp;
        this.level = level;
        this.alive = alive;
    }

    public void attack(Entity entity, Entity target){
        if (target != null && target.alive) {
            target.health = Math.max(0, target.health - entity.attackDamage);
        }
    }
}

// Was kann Player und was hat Player für Werte
class Player extends Entity{

    public Player(String name, int health, int maxHealth, int attackDamage, int coins, int exp, boolean alive, int level) {
        super(name, health, maxHealth, attackDamage, coins, exp, alive, level);
    }

    public void heal() {
        health = Math.min(maxHealth, health + 25 );
    }

    public void revive(){
        health = maxHealth;
        coins = 0;
        exp = 0;
    }

    public void buy(Item item, Inventory inventory){
        coins -= item.price;
        inventory.addItem(item);
    }

    public void sell(Item item, Inventory inventory){
        coins += item.sellPrice;
        inventory.removeItem(item);
    }
}

// hier wird Enemy erstellt und Angriff gesteuert
class Enemy extends Entity{

    public Enemy(String name, int health, int maxHealth, int attackDamage, int coins, int exp, boolean alive, int level) {
        super(name, health, maxHealth, attackDamage, coins, exp, alive, level);
    }

    public void revive(){health = maxHealth;}
}

class Boss extends Entity{

    public Boss(String name, int health, int maxHealth, int attackDamage, int coins, int exp, boolean alive, int level) {
        super(name, health, maxHealth, attackDamage, coins, exp, alive, level);
    }

    public void revive(){health = maxHealth;}
}

// Item Logik, hier werden Items erstellt und verwendet
class Item{
    String name;
    int attackDamage;
    int heal;
    int price;
    int sellPrice;
    boolean onetimeuse;

    public Item(String name, int attackDamage, int heal, int price, int sellPrice, boolean onetimeuse){
        this.name = name;
        this.attackDamage = attackDamage;
        this.heal = heal;
        this.price = price;
        this.sellPrice = sellPrice;
        this.onetimeuse = onetimeuse;
    }

    public void use(Player player, Entity entity)
    {
        if(this.heal > 0 && player.health <= player.maxHealth)
        {
            if (player.health <= Math.max(player.maxHealth, player.maxHealth - 100))
            {
                player.health = Math.min(player.maxHealth, player.health + this.heal);
            }
        }

        if (this.attackDamage > 0)
        {
            entity.health = Math.max(0, entity.health - attackDamage);
        }
    }
}

class Game{

    public void play() throws InterruptedException {
        // hier sind alle Klassen und Variablen, die ich oft gebraucht habe/wichtigsten
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        int plchoise = 4;
        // ran = random
        int ran;
        int ran3;
        int action;

        Player[] player = PlayerFactory.createPlayers();
        Enemy[] enemy = EnemyFactory.createEnemy();
        Item[] item = ItemFactory.createItem();
        Boss[] boss = BossFactory.createBoss();
        Fight fight = new Fight();
        Inventory inventory = new Inventory();
        Level level = new Level();
        Loot loot = new Loot();
        DevTools devTools = new DevTools();
        ShopCreater shopCreater = new ShopCreater();
        TUI tui = new TUI();

        // TUI
        tui.playerSelect();

        while (plchoise < 1 || plchoise > 3) {
            System.out.println("Waehle deine Klasse (1-3):");

            if (scanner.hasNextInt()) {
                plchoise = scanner.nextInt();

                if (plchoise < 0 || plchoise > 2) {
                    System.out.println("Bitte wähle 1, 2 oder 3!");
                }
            } else {
                System.out.println("Falsche Eingabe! Bitte gib eine Zahl ein.");
                scanner.next(); // Ungültigen Text überspringen
            }
        }

        plchoise--;
        System.out.println("Du hast " + player[plchoise].name + " Ausgewaehlt");

        // Cheat
        devTools.getAllItems(item, inventory);
        devTools.oneHit(player[plchoise]);
        devTools.godmode(player[plchoise]);

        // Kämpfen bis Player tot ist
        out: while(player[plchoise].health >= 0)
        {
            ran = random.nextInt(3);
            ran3 = random.nextInt(3);
            enemy[ran].revive();

            // Kämpfen bis Enemy tot ist
            fight: while (enemy[ran].health >= 0 || boss[ran3].health >= 0 || player[plchoise].health >= 0)
            {

                // Schauen ob Player noch lebt, wenn nicht = spiel beenden
                if (player[plchoise].health <= 0)
                {
                    player[plchoise].revive();
                    System.out.println
                            (
                                    "\nDu bist gestorben!" +
                                            "\nDas Spiel wird beendet!"
                            );
                    // break erste while-Schleife
                    break out;
                }

                // TUI
                tui.playerFight(player[plchoise], enemy[ran]);

                // Auf die auswahl des Players reagieren
                fight.fight(player[plchoise], enemy[ran], inventory, scanner);

                // wenn Enemy tot gibt es dem Player coins, exp und mit Glück chest und shop
                if (enemy[ran].health == 0)
                {
                    // TUI
                    System.out.println
                            (
                                    "Du hast ein " + enemy[ran].name + " besiegt und hast " + enemy[ran].coins + " Coins und " + enemy[ran].exp + " EP bekommen!" +
                                            "\nDruecke Taste ENTER um weiter zu spielen!"
                            );
                    loot.getEnemyDrop(player[plchoise], enemy[ran]);
                    level.levelUp(player[plchoise]);

                    int ran2 = random.nextInt(2);
                    int key2 = 1;

                    if (key2 == ran2)
                    {

                        System.out.println("Was ist das?");
                        Thread.sleep(1600);
                        System.out.println("Boss " + boss[ran3].name + " ist gespawnt");
                        Thread.sleep(888);

                        while (true)
                        {
                            tui.playerFightBoss(player[plchoise], boss[ran3]);

                            fight.fight(player[plchoise], boss[ran3], inventory, scanner);

                            if (boss[ran3].health <= 0)
                            {
                                System.out.println
                                        (
                                                "Du hast den Boss " + boss[ran3].name + " besiegt und hast " + boss[ran3].coins + " Coins und " + boss[ran3].exp + " EP bekommen!" +
                                                        "\nDruecke Taste ENTER um weiter zu spielen!"
                                        );

                                loot.getBossDrop(player[plchoise], boss[ran3]);
                                loot.chest(inventory, random);
                                boss[ran3].revive();
                                level.levelUp(player[plchoise]);

                                break;
                            }
                        }
                    }

                    loot.chest(inventory, random);

                    int ran1 = random.nextInt(5);
                    int key = 1;

                    if (key == ran1)
                    {
                        // TUI
                        tui.spawnShop();

                        shop:while (true)
                        {
                            if (scanner.hasNextInt())
                            {
                                action = scanner.nextInt();
                                if (action >= 1 || action <= 2)
                                {
                                    if (action == 1)
                                    {
                                        shopCreater.createShop(random, player[plchoise], inventory, scanner);
                                        break shop;
                                    }
                                    else
                                    {
                                        break shop;
                                    }
                                }
                                else
                                {
                                    System.out.println("Ungueltige Option! Nur Zahlen (1-2)");
                                }
                            }
                            else
                            {
                                System.out.println("Das ist keine Zahl!");
                                scanner.next();
                            }
                        }
                    }

                    break fight;
                }

                // Consolen schrubber(Console bereinigen)
                for (int i = 0; i < 50; i++) {
                    System.out.println();
                }
            }
        }
    }
}

// managed das inventar bzw. es lagert items
class Inventory{
    private ArrayList<Item> items = new ArrayList<>();

    public void addItem(Item item){
        items.add(item);
    }

    public Item getItem(int index){
        return items.get(index);
    }

    public void removeItem(Item item){
        items.remove(item);
    }

    public int size(){
        return items.size();
    }

    public void itemUse(){

    }
}

// Chest spawn Logik und Enemy Coin und exp Drops Logik
class Loot{
    Scanner scanner = new Scanner(System.in);

    public void chest(Inventory inventory, Random random){
        int key = random.nextInt(4);
        if (key == 1)
        {
            // TUI
            Item randomItem = ItemFactory.getRandom(random);
            inventory.addItem(randomItem);
            System.out.println("DU hast ein Chest gefunden, sehen wir nach was drinnen ist...");
            System.out.println(
                    "Du hast " + randomItem.name + " gefunden!" +
                            "\nDruecke Taste ENTER um weiter zu spielen!"
            );
            scanner.nextLine();
        }
    }

    public void getEnemyDrop(Player player, Enemy enemy){
        player.coins += enemy.coins;
        player.exp += enemy.exp;
        scanner.nextLine();
    }

    public void getBossDrop(Player player, Boss boss){
        player.coins += boss.coins;
        player.exp += boss.exp;
        scanner.nextLine();
    }
}

// hier werden Player Charakter erstellt
class PlayerFactory{

    public static Player[] createPlayers() {
        return new Player[] {
                new Player("Warrior", 450, 450, 15, 0, 0, true, 1),
                new Player("Mage", 260, 260, 35, 0, 0, true, 1),
                new Player("Archer", 350, 350, 10, 0, 0, true, 1)
        };
    }
}

// hier werden Bosse erstellt
class BossFactory{
    public static Boss[] createBoss() {
        return new Boss[] {
                new Boss("Ignis", 700, 700, 35, 90, 85, true, 1),
                new Boss("Vargus", 1000, 1000, 30, 120, 110, true, 1),
                new Boss("Skarrg", 1350, 1350, 25, 200, 180, true, 1)
        };
    }
}

// hier werden Enemys erstellt
class EnemyFactory{

    public static Enemy[] createEnemy() {
        return new Enemy[] {
                new Enemy("Zombie", 100, 100, 25, 5, 20, true, 0),
                new Enemy("Goblin", 140, 140, 27, 8, 25, true, 0),
                new Enemy("Dragon", 300, 300, 32, 15, 60, true, 0),
        };
    }
}

// hier werden alle items erstellt, hier mit Randomizer Logik
class ItemFactory{

    public static Item[] createItem() {
        return new Item[] {
                new Item("OneShot Item (1000 AD)", 1000, 0, 199, 185, true),
                new Item("Heiltrank (+100 HP)", 0, 100, 35, 15, true),
                new Item("Trank des Schadens (90 AD)", 90, 0, 15, 20, true),
                new Item("Heavy Sword (120 AD | -10 HP)", 120, -10, 70, 55, false),
                new Item("Sword (55 AD | -6 HP)", 55, -6, 35, 20, false),
                new Item("Wood Sword (25 AD)", 25, 0, 10, 20, false),
                // new Item("CleanItem", 1000, 1000, 1000, 1000),
        };
    }

    public static Item getRandom(Random random){
        Item[] items = createItem();

        int ran = random.nextInt(items.length);

        return items[ran];
    }
}

// hiermit wird der Shop erstellt
class ShopCreater{

    public Item[] createShop(Random random, Player player, Inventory inventory, Scanner scanner) {
        int action;
        Item[] shopItems = new Item[3];
        shopItems[0] = ItemFactory.getRandom(random);
        shopItems[1] = ItemFactory.getRandom(random);
        shopItems[2] = ItemFactory.getRandom(random);

        while (true) {
            for (int i = 0; i < 3; i++) {System.out.println();}

            System.out.println("Coins: " + player.coins);
            System.out.println("=== Shopify ===");
            System.out.println("(" + 1 + ") " + shopItems[0].name + " (" + shopItems[0].attackDamage + "AD | " + shopItems[0].heal + " HP)" + "   Coins: " + shopItems[0].price);
            System.out.println("(" + 2 + ") " + shopItems[1].name + " (" + shopItems[1].attackDamage + "AD | " + shopItems[1].heal + " HP)" + "   Coins: " + shopItems[1].price);
            System.out.println("(" + 3 + ") " + shopItems[2].name + " (" + shopItems[2].attackDamage + "AD | " + shopItems[2].heal + " HP)" + "   Coins: " + shopItems[2].price);
            System.out.println("(" + 4 + ") Exit");
            System.out.println("===============");

            action:while (true)
            {
                if (scanner.hasNextInt())
                {
                    action = scanner.nextInt();
                    if (action >= 1 && action <= 4)
                    {
                        if (4 == action) {
                            break action;
                        }
                        else
                        {
                            action -= 1;
                            if (player.coins >= shopItems[action].price) {
                                player.buy(shopItems[action], inventory);
                                System.out.println("\nThank You :)");
                                break action;
                            } else {
                                System.out.println("Nicht genuegend Geld!");
                            }
                        }
                    }
                    else
                    {
                        System.out.println("Ungueltige Option! Nur Zahlen (1-4)");
                    }
                }
                else
                {
                    System.out.println("Das ist keine Zahl!");
                    scanner.next();
                }
            }
            return shopItems;
        }
    }
}

// hier wird exp zu lvl konvertiert
class Level{
    public void levelUp(Player player){
        while (player.exp >= player.expToNextLevel) {

            player.exp -= player.expToNextLevel;
            player.level++;

            player.expToNextLevel += 50;

            player.coins += 20;

            System.out.println("LEVEL UP!");
            System.out.println("Dein Level: " + player.level);
            System.out.println("EXP fuer naechstes Level: " + player.expToNextLevel);
        }

    }
}

class Fight{
    public void fight(Player player, Entity entity, Inventory inventory, Scanner scanner) {
        fight: while (true)
        {
            System.out.println("Waehle deine aktion (1-4):");

            if (scanner.hasNextInt()) {
                int action;
                action = scanner.nextInt();

                if (action >= 1 || action <= 4) {
                    switch (action) {
                        case 1:
                            // Angriff Logik
                            entity.attack(player, entity);

                            if (entity.health <= 0) {
                                break;
                            } else {
                                entity.attack(entity, player);
                            }
                            // TUI
                            System.out.println
                                    (
                                            "Der Gegner hat noch " + entity.health + "/" + entity.maxHealth + " HP" +
                                                    "\nDu hast " + entity.attackDamage + " schaden bekommen. Du hast noch " +
                                                    player.health + "/" + player.maxHealth + " HP"
                                    );
                            break;

                        case 2:
                            // Player heal Logik
                            player.heal();

                            break;

                        case 3:
                            // Inventar Logik
                            int z = 1;
                            int actionInv;
                            for (int i = 0; i < inventory.size(); i++) {
                                Item UpdatetItem = inventory.getItem(i);
                                System.out.println("(" + z + ")" + UpdatetItem.name);
                                z++;
                            }

                            System.out.println("(" + z + ")" + "Exit");

                            inv:
                            while (true) {
                                if (scanner.hasNextInt()) {
                                    actionInv = scanner.nextInt();

                                    if (actionInv >= 1 && actionInv <= z) {
                                        if (actionInv == z) {
                                            break inv;
                                        }
                                        actionInv--;

                                        Item selectedItem = inventory.getItem(actionInv);
                                        selectedItem.use(player, entity);

                                        if (inventory.getItem(actionInv).onetimeuse == true) {
                                            inventory.removeItem(selectedItem);
                                        }
                                        if (entity.health > 0) {
                                            entity.attack(player, entity);
                                        }
                                        break inv;
                                    } else {
                                        System.out.println("Ungueltige Option! Nur Zahlen (1-" + z + ")");
                                    }
                                } else {
                                    System.out.println("Das ist keine Zahl!");
                                    scanner.nextLine();
                                }
                            }

                            break;
                        case 4:
                            // fliehen / exit game
                            break fight;
                    }
                    break fight;
                }
                else
                {
                    System.out.println("Ungueltige Option! Nur Zahlen (1-4)");
                }
            }
            else
            {
                System.out.println("Das ist keine Zahl!");
                scanner.next();
            }
        }
    }
}

// Cheats ;)
class DevTools{
    public void getAllItems(Item[] items, Inventory inventory){
        for (int i = 0; i < items.length; i++) {
            inventory.addItem(items[i]);
        }
    }

    public void godmode(Player player){
        player.health = player.maxHealth;
    }

    public void oneHit(Player player){
        player.attackDamage = 1000000;