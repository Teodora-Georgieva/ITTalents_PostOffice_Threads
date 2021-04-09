package post;

import com.google.gson.Gson;
import post.db.DBConnector;
import post.people.Citizen;
import post.people.Person;
import post.people.Postman;
import post.shipments.Letter;
import post.shipments.Package;
import post.shipments.Shipment;
import post.utils.Generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Post {
    private ArrayList<Postman> postmen;
    private ArrayList<Shipment> storage;
    private TreeMap<LocalDate, TreeMap<LocalTime, ArrayList<Shipment>>> archive;
    private Town town;
    private HashMap<Person.PersonType, ArrayList<Integer>> personTypesAndIDs;
    private double money;

    public Post(Town town){
        this.money = 0;
        this.town = town;
        this.postmen = new ArrayList<>();
        this.storage = new ArrayList<>();
        this.archive = new TreeMap<>();
        this.personTypesAndIDs = new HashMap<>();
        this.personTypesAndIDs.put(Person.PersonType.CITIZEN, new ArrayList<>());
        this.personTypesAndIDs.put(Person.PersonType.POSTMAN, new ArrayList<>());
    }

    public void addPostman(Postman postman){
        this.postmen.add(postman);
    }

    public int getRandomCitizenId(){
        int citizens = this.personTypesAndIDs.get(Person.PersonType.CITIZEN).size();
        int citizenId = Generator.generateRandomNumber(0, citizens - 1);
        return citizenId;
    }

    public synchronized ArrayList<Letter> receiveLettersFromPickers(){
        while(this.storage.size() >= 50) {
            try {
                System.out.println(Thread.currentThread().getName() + " waits because there are " + this.storage.size() +
                        " shipments in the post");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        MailBox mailBox = null;
       // synchronized (this) {
            if (!town.allBoxesAreEmpty() && town.hasFreeMailBox()) {
                mailBox = this.town.giveMailBoxToLetterPicker();
            }

        //}

        if(mailBox == null){
            if(this.storage.size() < 50) {
                notifyAll();
            }
            return null;
        }


        ArrayList<Letter> letters = mailBox.emptyMailBox();
        //synchronized (this) { //this or other object?
        if(this.storage.size() >= 50) {
            notifyAll();
        }
       // }
        return letters;
    }

    public synchronized ArrayList<Shipment> deliverShipments(){ //synchronized ????
        while (this.storage.size() < 50){
            try {
                System.out.println(Thread.currentThread().getName() + " waits because there are " + this.storage.size() +
                                   " shipments in the post");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Shipment> shipments = new ArrayList<>();

        int countOfShipments = this.storage.size() / getCountOfFreePostmen();
        if (countOfShipments == 0) {
            try {
                System.out.println(Thread.currentThread().getName() + " waits because there are not enough shipments to deliver");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            if(countOfShipments > this.storage.size()){
                countOfShipments = this.storage.size();
            }

            for (int i = 0; i < countOfShipments; i++) {
                shipments.add(this.storage.remove(i));
            }
        }

        if(this.storage.size() < 50) {
            notifyAll();
        }
        return shipments;
    }

    private int getCountOfFreePostmen(){
        int count = 0;
        for (int i = 0; i < this.postmen.size(); i++) {
            Postman postman = this.postmen.get(i);
            if(postman.getJobTitle() == Postman.JobTitle.POSTMAN && postman.isFree()){
                count++;
            }
        }

        return count;
    }

    public synchronized void addToArchive(LocalDate localDate, LocalTime localTime, ArrayList<Shipment> shipments){
        System.out.println("ADDED TO ARCHIVE");
        if(!this.archive.containsKey(localDate)){
            this.archive.put(localDate, new TreeMap<>());
        }

        TreeMap<LocalTime, ArrayList<Shipment>> shipmentsByDay = this.archive.get(localDate);
        if(!shipmentsByDay.containsKey(localTime)){
            shipmentsByDay.put(localTime, new ArrayList<>());
        }

        ArrayList<Shipment> shipmentsToAdd = shipmentsByDay.get(localTime);
        shipmentsToAdd.addAll(shipments);
    }

    public synchronized void addToStorage(ArrayList<Shipment> shipments) {
        System.out.println("ADDED TO STORAGE");
        this.storage.addAll(shipments);
    }

    public synchronized void receivePackages(ArrayList<Shipment> packages) { //synchronized - yes
        System.out.println(Thread.currentThread().getName() + " added " + packages.size() + " packages to post");
        this.storage.addAll(packages);
    }

    public synchronized void receiveLettersFromCitizens(ArrayList<Shipment> letters) {
        System.out.println(Thread.currentThread().getName() + " added " + letters.size() + " letters to post");
        this.storage.addAll(letters);
    }

    public void addToPersonTypesAndIDs(Person person){
        Person.PersonType personType = person.getPersonType();
        int id = person.getPersonID();
        ArrayList<Integer> ids = this.personTypesAndIDs.get(personType);
        ids.add(id);
    }

    public synchronized void receiveMoney(double money) {
        this.money += money;
    }

    public void writeShipmentsStoryToArchive(int choice) {
        if (choice == 0) {
            this.writeShipmentsToFile(1);
        }
        else{
            this.writeShipmentsToDB();
        }
    }

    private void writeShipmentsToDB(){
        Connection connection = DBConnector.getInstance().getConnection();

        for(LocalDate localDate : this.archive.keySet()){
            if(localDate.compareTo(LocalDate.now()) == 0){
                TreeMap<LocalTime, ArrayList<Shipment>> treeMap = this.archive.get(localDate);
                for(ArrayList<Shipment> shipments : treeMap.values()){
                    for(Shipment shipment : shipments){
                        int senderID = shipment.getSenderData().getId();
                        Citizen sender = town.getCitizenById(senderID);
                        int receiverID = shipment.getReceiverData().getId();
                        Citizen receiver = town.getCitizenById(receiverID);
                        this.insertIntoReceivers(connection, receiverID, receiver.getFirstName(),
                                                 receiver.getLastName(), receiver.getAddress());
                        this.insertIntoSenders(connection, senderID, sender.getFirstName(),
                                                  sender.getLastName(), sender.getAddress());
                        this.insertIntoShipments(connection, shipment.getShipmentType().toString(),
                                      shipment.getStatuses().toString(), senderID, receiverID, shipment.getDate());
                    }
                }
            }
        }
    }

    private void writeShipmentsToFile(int fileNumber){
        //txt files - works
        /*
        File file = new File("File" + fileNumber++ + ".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating file");
        }
        for (LocalDate localDate : this.archive.keySet()) {
            if (localDate.compareTo(LocalDate.now()) == 0) {
                try (PrintStream ps = new PrintStream(file)) {
                    TreeMap<LocalTime, ArrayList<Shipment>> treeMap = this.archive.get(localDate);
                    for (ArrayList<Shipment> shipments : treeMap.values()) {
                        for (Shipment shipment : shipments) {
                            ps.println(shipment.getShipmentType() + ", sender: " + shipment.getSenderData() +
                                    ", receiver: " + shipment.getReceiverData() + ", statuses: " +
                                    shipment.getStatuses());
                        }
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                }
            }
        }
         */

        //json files
        File file = new File("File" + fileNumber++ + ".json");
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating file");
        }

        Gson gson = new Gson();

        //doesn't make a valid json
        for (LocalDate localDate : this.archive.keySet()) {
            if (localDate.compareTo(LocalDate.now()) == 0) {
                try (PrintStream ps = new PrintStream(file)) {
                    TreeMap<LocalTime, ArrayList<Shipment>> treeMap = this.archive.get(localDate);
                    for (ArrayList<Shipment> shipments : treeMap.values()) {
                        for (Shipment shipment : shipments) {
                            /*
                            ps.println(shipment.getShipmentType() + ", sender: " + shipment.getSenderData() +
                                    ", receiver: " + shipment.getReceiverData() + ", statuses: " +
                                    shipment.getStatuses());

                             */

                            String json = gson.toJson(shipment);
                            ps.println(json);
                            ps.println();
                        }
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                }
            }
        }
    }

    private void insertIntoShipments(Connection connection, String shipmentType, String statuses,
                                     int senderID, int receiverID, LocalDate localDate){
        String insertIntoShipments = "INSERT INTO shipments (type, status, sender_id, receiver_id, date) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement ps = connection.prepareStatement(insertIntoShipments, Statement.RETURN_GENERATED_KEYS);){
            ps.setString(1, shipmentType);
            ps.setString(2, statuses);
            ps.setInt(3, senderID);
            ps.setInt(4, receiverID);
            ps.setDate(5, Date.valueOf(localDate));
            ps.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println("Exception in insert into shipments() - " + throwables.getMessage());
        }
    }

    private void insertIntoReceivers(Connection connection, int id, String firstName, String lastName, String address){
        String insertIntoShipments = "INSERT INTO receivers (id, first_name, last_name, address) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(insertIntoShipments, Statement.RETURN_GENERATED_KEYS);){
            ps.setInt(1, id);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, address);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            //System.out.println("Exception in insertIntoReceivers() - " + throwables.getMessage());
        }
    }

    private void insertIntoSenders(Connection connection, int id, String firstName, String lastName, String address){
        String insertIntoShipments = "INSERT INTO senders (id, first_name, last_name, address) VALUES (?, ?, ?, ?)";

        try(PreparedStatement ps = connection.prepareStatement(insertIntoShipments, Statement.RETURN_GENERATED_KEYS);){
            ps.setInt(1, id);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, address);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            //System.out.println("Exception in insertIntoSenders() - " + throwables.getMessage());
        }
    }

    public void makeInquiry(){
        Connection connection = DBConnector.getInstance().getConnection();
        this.selectAllShipmentsByDate(connection, LocalDate.now());
        this.selectLetterPercentageByDay(connection, LocalDate.now());
        //this.selectPercentageOfFragilePackages(connection);
        //this.selectCountOfShipmentsByPostman();
    }

    private void selectAllShipmentsByDate(Connection connection, LocalDate localDate){
        String query = "SELECT * FROM shipments WHERE date = ?";

        try(PreparedStatement ps = connection.prepareStatement(query);){
            ps.setDate(1, Date.valueOf(localDate));
            ResultSet rows = ps.executeQuery();
            System.out.println("------------- SHOWING ALL SHIPMENTS BY DATE -------------");
            while (rows.next()){
                System.out.println("id: " + rows.getInt(1) + ", type: " + rows.getString(2) +
                        ", status: " + rows.getString(3) + ", sender id: " + rows.getInt(4) +
                        ", receiver id: " + rows.getInt(5) + ", date: " + rows.getDate(6));
            }
            System.out.println("----------------------------------------------------------");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void selectLetterPercentageByDay(Connection connection, LocalDate localDate){
        String selectCountOfLetters = "SELECT COUNT(*) FROM shipments WHERE type = ? AND date = ?";
        String selectCountOfShipments = "SELECT COUNT(*) FROM shipments WHERE date = ?";

        try(PreparedStatement ps1 = connection.prepareStatement(selectCountOfLetters);
            PreparedStatement ps2 = connection.prepareStatement(selectCountOfShipments);){
            ps1.setString(1, "LETTER");
            ps1.setDate(2, Date.valueOf(localDate));
            ResultSet rows1 = ps1.executeQuery();
            rows1.next();
            int countOfLetters = rows1.getInt(1);

            ps2.setDate(1, Date.valueOf(localDate));
            ResultSet rows2 = ps2.executeQuery();
            rows2.next();
            int countOfShipments = rows2.getInt(1);

            double percentage = (double)countOfLetters/countOfShipments*100;
            System.out.println("PERCENTAGE OF LETTERS : " + percentage);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void selectPercentageOfFragilePackages(Connection connection){
        //todo
    }

    private void selectCountOfShipmentsByPostman(){
        //todo
    }
}