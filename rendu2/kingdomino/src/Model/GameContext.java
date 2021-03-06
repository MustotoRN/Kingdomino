package Model;

import java.util.*;
import java.util.Map.Entry;

public class GameContext
{
    private PlayerStrategy nbPlayersStrat; //stratégie associé au nombre de joueurs
    private GameMode gameMode; //stratégie associé au mode de jeu choisi
    private Deck deck; // Packet de tuiles utilisé pour la partie
    private Map<Tile, King> currentTiles = new HashMap<>(); // Tuiles correspondante aux choix diponible pour les joueurs
    private ArrayList<GameObserver> observers = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();

    //Liste des king du round actuel
    private ArrayList<King> kings = new ArrayList<>();
    //Liste des king pour le prochain round
    private ArrayList<King> nextRoundKings = new ArrayList<>();

    //Listes modes de jeu présent dans la partie
    private ArrayList<GameMode> gamesModes = new ArrayList<>();

    private int turn;

    public GameContext()
    {
        turn = 0;
    }

    public void destroy(){
        this.nbPlayersStrat = null;
        this.gameMode = null;
        this.deck = null;
        this.currentTiles.clear();
        this.players.clear();
        this.kings.clear();
        this.nextRoundKings.clear();
        this.gamesModes.clear();
        this.turn = 0;
    }




    public Map<Tile, King> getCurrentTiles() {
        return currentTiles;
    }

    public void addObserver(GameObserver gameObserver)
    {
        observers.add( gameObserver );
    }

    //Implémentation de la variable d'instance nbPlayersStrat en fonction du nombre de joueur
    public void setPlayerStrategy(int nbPlayers)
    {
        switch (nbPlayers) {
            case 2 -> nbPlayersStrat = new TwoPlayers();
            case 3 -> nbPlayersStrat = new ThreePlayers();
            case 4 -> nbPlayersStrat = new FourPlayers();
        }
    }

    public GameMode getGameMode(){
        return this.gameMode;
    }

    public PlayerStrategy getNbPlayersStrat(){
        return this.nbPlayersStrat;
    }

    //Implémentation de la variable d'instance gameMode en fonction du mode de jeu
    public void setGameStrategy(int gameModeChoosen)
    {
        GameMode normalMode = new NormalMode();
        this.gameMode = normalMode;
        switch (gameModeChoosen) {
            case 1 -> {
                gameMode = new Harmony(normalMode);
                this.gamesModes.add( new Harmony(normalMode) );
            }
            case 2 -> {
                gameMode = new MiddleKingdom(normalMode);
                this.gamesModes.add( new MiddleKingdom(normalMode) );
            }
            case 3 -> {
                gameMode = new MiddleKingdom( new Harmony(normalMode));
                this.gamesModes.add( new MiddleKingdom(normalMode) );
                this.gamesModes.add( new Harmony(normalMode) );
            }
        }
    }

    //Retourne vrai si la partie contient le mode de jeu Harmony
    //Méthode conseiller par le prof
    public boolean isHarmony(){
        for (GameMode gameMode: this.gamesModes
             ) {
            if(gameMode.hasHarmony()){
                return true;
            }
        }
        return false;
    }


    public void initGame(ArrayList<KingColor> colors, ArrayList<String> pseudo)
    {
        createDeck();
        createPlayers(colors, pseudo);
        pickTiles();
    }

    private ArrayList<Player> getPlayers(){
        return new ArrayList<>(this.players);
    }

    public Player getPlayer(int i){
        return this.getPlayers().get(i);
    }

    public int getPlayersNb(){
        return this.getPlayers().size();
    }


    //Création du packet de tuiles de la bonne taille
    private void createDeck()
    {
        this.deck = new Deck(nbPlayersStrat.getnbTile());
    }

    private void createKing(KingColor color, int number, Player player)
    {
        for (int i = 0; i< number ; i++){
            this.kings.add( new King(color, player) );
        }
    }

    private void createPlayers(ArrayList<KingColor> colors, ArrayList<String> pseudo)
    {
        int nbPlayer = nbPlayersStrat.getnbBoard();
        ArrayList<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i< nbPlayer ; i++){
            KingColor color = colors.get(i);
            String pName = pseudo.get(i);
            Player newPlayer = new Player( color, pName, new PlayerBoard());
            if(nbPlayer ==2){
                this.createKing( color , 2, newPlayer);
            }else{
                this.createKing( color, 1, newPlayer );
            }
            newPlayers.add ( newPlayer );
        }


        Collections.shuffle( this.kings );

        //Inversion du king n°2 avec le n°3 si le meme joueur joue 2 fois en premier pour + d'équité
        if(this.kings.get(0).getColor() == this.kings.get(1).getColor()){
            King temp = this.kings.get(2);
            this.kings.set(2, this.kings.get(1));
            this.kings.set( 1, temp );
        }
        Collections.shuffle( newPlayers );
        this.players = newPlayers;
    }

    public Player getPlayerCastleTurn(){
        return this.players.get( turn % this.players.size() );
    }

    public King getKingTurn(){
        return this.kings.get(turn % this.kings.size() );
    }

    public int getTurn(){
        return this.turn;
    }


    public void pickTiles()
    {
        currentTiles.clear();
        for (int i = 0; i< nbPlayersStrat.getnbKings(); i++ ){
            if (deck.getNbTiles() != 0) {
                currentTiles.put(deck.getTile(), null);
            }
        }
        if(currentTiles.isEmpty()){
            this.notifyObserversEnd();
        }


        /***ORDER TILES BY THEIR NUMBER***/
        TreeMap<Tile, King> sorted = new TreeMap<>(this.currentTiles);
        this.currentTiles = sorted;
    }

    public boolean setCastle(Player player,int x, int y)
    {
        Castle castle = new Castle();
        if(player.getBoard().setCastle(x, y, castle)) {
            this.turn++;
            notifyObservers();
            return true;
        }
        return false;
    }

    /**
     * @param x, y : Position du terrain gauche de la tuile
     * @return TRUE si nous avons reussi à placer la tuile
     */
    public boolean setTile(int x, int y)
    {
        //Si toutes les tuiles ont été choisi
        if(!allTilesChoosen()){
            return false;
        }

        Player player = this.getKingTurn().getPlayer();
        Tile tile = this.getKingTurn().getTile();
        Map< Tile, King > currentTiles = this.getCurrentTiles();

        /* Ancienne facon pour obtenir la tuile du joueur qu'il doit jouer
        for ( Map.Entry<Tile, Player> choosenTile : currentTiles.entrySet()) {
            if(choosenTile.getValue() == player){
                tile = choosenTile.getKey();
                break;
            }
        }*/

        if(player.getBoard().setTile(  x,  y, tile.getDirection(), tile  )) {
            currentTiles.remove(tile);
            this.getKingTurn().removeTile();
            turn++;
            if(currentTiles.size() == 0){
                this.pickTiles();
            }
            notifyObservers();
            return true;
        }
        return false;
    }

    public boolean allTilesChoosen() {
        for ( Map.Entry<Tile, King> choosenTile : this.currentTiles.entrySet()) {
            if(choosenTile.getValue() == null){
                return false;
            }
        }
        return true;
    }

    public void chooseTile(Tile tile) {
        currentTiles.replace(tile, this.getKingTurn());
        this.getKingTurn().setChoosenTile(tile);
        turn++;

        //Si toutes les tuiles sont choisi et que le nombre de tuile est au meme nombre de roi
        // (S'execute une fois que tout le monde a choisi ces tuiles)
        if(this.allTilesChoosen() && this.currentTiles.size() == this.getNbPlayersStrat().getnbKings()){
            this.orderNextRoundKings();
        }
        this.notifyObservers();
    }

    private void orderNextRoundKings(){
        // Tuile+Roi qu'il l'a choisi
        Map<Tile, King> tiles = new HashMap<>(this.currentTiles);
        int min;
        int tileNumber;
        King king = null;
        Tile tile = null;
        for (int i = 0; i<this.kings.size(); i++){
            //Récupere la tuile avec la plus petite valeur
            min = 10000;

            for ( Map.Entry<Tile, King> choosenTile : tiles.entrySet()) {
                tileNumber = choosenTile.getKey().getNumber();
                if(min > tileNumber){
                    min = tileNumber;
                    king = choosenTile.getValue();
                    tile = choosenTile.getKey();
                }
            }

            //Ajout du roi qui a pris cette tuile
            this.nextRoundKings.add( king );

            //Suppression de cette tuile là (dans notre liste temporaire)
            tiles.remove(tile);
        }

        //Met le nouvelle ordre des rois
        if( !this.nextRoundKings.isEmpty() ){
            this.kings = new ArrayList<>(this.nextRoundKings);
        }
        this.nextRoundKings.clear();
    }

    public void rotateCurrentTile(){
        this.getKingTurn().getTile().rotate();
        notifyObservers();
    }

    public void reverseCurrentTile(){
        this.getKingTurn().getTile().reverse();
        notifyObservers();
    }

    //Saute ce tour là
    public void skipTurn(){
        Tile currentTile = null;
        for ( Map.Entry<Tile, King> choosenTile : this.currentTiles.entrySet()) {
            if(this.getKingTurn() == choosenTile.getValue()){
                currentTile = choosenTile.getKey();
            }
        }
        this.currentTiles.remove( currentTile );
        this.getKingTurn().removeTile();
        turn++;
        if(currentTiles.size() == 0){
            this.pickTiles();
        }
        notifyObservers();
    }

    public Map<Player, Integer> getPlayersRank(){
        Map<Player, Integer> players = new HashMap<>();

        int i =0;
        for (Player player : this.getPlayers()){
            players.put( player, this.gameMode.calculateScore(player.getBoard()) );
            i++;
        }



        /** Trie du dictionnaire Player -> Points **/
        List<Player> mapKeys = new ArrayList<>(players.keySet());
        List<Integer> mapValues = new ArrayList<>(players.values());
        Collections.sort(mapValues, Collections.reverseOrder());

        LinkedHashMap<Player, Integer> sortedMap = new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<Player> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Player key = keyIt.next();
                Integer comp1 = players.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }





    private void notifyObservers(){
        for (GameObserver observer : this.observers){
            observer.update(this);
        }
    }

    private void notifyObserversEnd(){
        for (GameObserver observer : this.observers){
            observer.updateEnd(this);
        }
    }

}
