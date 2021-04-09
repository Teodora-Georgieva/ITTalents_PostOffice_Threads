package post.shipments;

import post.people.CitizenData;
import post.utils.Validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class Shipment {
    public enum ShipmentType{
        LETTER, PACKAGE
    }

    public enum Status{
        NORMAL, FRAGILE, ONEROUS, NONE
    }

    //private int senderId; //?
    //private int receiverId; //?
    private CitizenData senderData;
    private CitizenData receiverData;
    ///private HashMap<String, String> senderData;
    //private HashMap<String, String> receiverData;
    private LocalDate date;
    private LocalTime time;
    private ArrayList<Status> statuses;
    private int postmanID;
    //private ShipmentType shipmentType;

    public Shipment(CitizenData senderData, CitizenData receiverData){
        this.senderData = senderData;
        this.receiverData = receiverData;
        this.statuses = new ArrayList<>();
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }
    /*
    public Shipment(int senderId, int receiverId){
        if(Validator.isValidID(senderId)){
            this.senderId = senderId;
        }
        else {
            this.senderId = 1;
        }

        if(Validator.isValidID(receiverId) && senderId != receiverId) {
            this.receiverId = receiverId;
        }
        else{
            this.receiverId = 2;
        }

        this.statuses = new ArrayList<>();
        //todo produljenie na konstruktora
    }

     */

    public abstract ShipmentType getShipmentType();
    public abstract double getTax();

    public LocalDate getDate() {
        return date;
    }

    public void setPostmanID(int postmanID) {
        this.postmanID = postmanID;
    }

    void addStatus(Status status){
        this.statuses.add(status);
    }

    public String getReceiverAddress(){
        return this.receiverData.getAddress();
    }

    public CitizenData getSenderData() {
        return this.senderData;
    }

    public CitizenData getReceiverData() {
        return this.receiverData;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }
}