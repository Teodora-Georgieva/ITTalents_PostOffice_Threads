package post;

import post.people.Citizen;
import post.utils.Generator;
import post.utils.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

public class Town {
    private String name;
    private ArrayList<MailBox> mailBoxes;
    private Post post;
    private TreeMap<Integer, Citizen> idsAndCitizens;
    private ArrayList<Citizen> citizens;
    public Town(String name){
        if(Validator.isValidString(name)){
            this.name = name;
        }
        else{
            this.name = "Sofia";
        }

        this.mailBoxes = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            this.mailBoxes.add(new MailBox());
        }

        this.idsAndCitizens = new TreeMap<>();
        this.citizens = new ArrayList<>();
    }


    public void addCitizenAndID(Citizen citizen){
        this.idsAndCitizens.put(citizen.getPersonID(), citizen);
    }


    public Citizen getCitizenById(int id){
        return this.idsAndCitizens.get(id);
    }


    public void setPost(Post post) {
        this.post = post;
    }

    public void addCitizen(Citizen citizen){
        this.citizens.add(citizen);
    }

    public Citizen getRandomCitizen(){
        int idx = Generator.generateRandomNumber(0, this.citizens.size() - 1);
        return this.citizens.get(idx);
    }

    /*
    public synchronized MailBox giveMailBoxToCitizen(){
        for(MailBox mailBox : this.mailBoxes){
            if(mailBox.isEmpty() || !mailBox.isFull()){
                return mailBox;
            }
        }

        return null;
    }

     */

    public boolean allBoxesAreEmpty(){
        for (int i = 0; i < this.mailBoxes.size(); i++) {
            MailBox crrBox = this.mailBoxes.get(i);
            if(!crrBox.isEmpty()){
                return false;
            }
        }

        return true;
    }

    public boolean hasFreeMailBox(){
        for (int i = 0; i < this.mailBoxes.size(); i++) {
            MailBox crrBox = this.mailBoxes.get(i);
            if(!crrBox.isFree()){
                return true;
            }
        }
        return false;
    }

    public MailBox giveMailBoxToLetterPicker(){
        for (int i = 0; i < this.mailBoxes.size(); i++) {
            MailBox crrBox = this.mailBoxes.get(i);
            if(!crrBox.isEmpty() && crrBox.isFree()){
                crrBox.setBusy();
                return crrBox;
            }
        }

        return null;
    }

    //??????????????
    public MailBox giveMailBoxToCitizen() { //???? synchronized - yes, bcz many citizens could try to get the
                                           //same box and this should not happen
                                           //but maybe not necessary because add letter to box is synchronized

        int mailBoxIdx = Generator.generateRandomNumber(0, 24);
        return this.mailBoxes.get(mailBoxIdx);
    }

    public Collection<Citizen> getCitizens() {
        return Collections.unmodifiableCollection(this.citizens);
    }
}