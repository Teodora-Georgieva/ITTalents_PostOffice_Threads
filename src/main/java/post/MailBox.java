package post;

import post.shipments.Letter;

import java.util.ArrayList;

public class MailBox {
    private static int id = 1;
    private int mailBoxID;
    private String location;
    private ArrayList<Letter> letters;
    private volatile boolean isEmpty;
    private volatile boolean isFree;

    public MailBox(){
        this.mailBoxID = id++;
        this.letters = new ArrayList<>();
        this.isEmpty = true;
        this.location = "MailBoxLocation" + this.mailBoxID;
    }

    public synchronized void addLetter(Letter letter){ //synchronized because many citizens could try to put in 1 box
                                                       //at the same time
        this.letters.add(letter);
        System.out.println(Thread.currentThread().getName() + " added letter to mailBox " + this.mailBoxID +
                                " on location " + this.location);
        this.isEmpty = false;
    }

    public ArrayList<Letter> emptyMailBox(){
        ArrayList<Letter> letters = new ArrayList<>();
        letters.addAll(this.letters);
        this.letters = null;
        this.letters = new ArrayList<>();
        this.isEmpty = true;
        this.isFree = true;
        return letters;
    }

    public boolean isEmpty(){
        return this.isEmpty;
    }

    public boolean isFree() {
        return this.isFree;
    }

    public void setBusy() {
        this.isFree = false;
    }
}