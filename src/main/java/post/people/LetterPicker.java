package post.people;

import post.shipments.Letter;
import post.shipments.Shipment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class LetterPicker extends Postman{
    private ArrayList<Shipment> bagWithLetters;

    public LetterPicker(String firstName, String lastName) {
        super(firstName, lastName);
        this.experience = 1;
        this.bagWithLetters = new ArrayList<>();
    }

    @Override
    public JobTitle getJobTitle() {
        return JobTitle.LETTER_PICKER;
    }

    @Override
    public void run() {
        while (true){
            ArrayList<Shipment> receivedLetters = new ArrayList<>();
            ArrayList<Letter> letters = post.receiveLettersFromPickers();
            if(letters != null) {
                receivedLetters.addAll(letters);
            }

            if(!(receivedLetters == null || receivedLetters.isEmpty())) {
                this.addLettersToBag(receivedLetters);
            }
            else{
                System.out.println("Letter picker" + this.getFirstName() + " " + this.getLastName() + " hasn't picked any letters");
                continue;
            }

            try {
                Thread.sleep(20000); //obhodut produljava 2 chasa
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            post.addToArchive(LocalDate.now(), LocalTime.now(), bagWithLetters);
            post.addToStorage(bagWithLetters);
            this.bagWithLetters = null;
            this.bagWithLetters = new ArrayList<>();
        }
    }

    private void addLettersToBag(ArrayList<Shipment> letters){
        this.bagWithLetters.addAll(letters);
    }
}