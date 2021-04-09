package post;

import post.people.Citizen;
import post.people.LetterPicker;
import post.people.Person;
import post.people.Postman;

import java.util.Scanner;

public class Demo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.print("Please, enter 1 for DB backup and 0 for File backup: ");
            choice = sc.nextInt();
        }while(!(choice == 1 || choice == 0));

        final int finalChoice = choice;

        Town pernik = new Town("Pernik");
        Post post = new Post(pernik);
        pernik.setPost(post);
        Person.town = pernik;
        Person.post = post;

        for (int i = 1; i <= 10; i++) {
            Citizen citizen = new Citizen("Citizen", String.valueOf(i));
            pernik.addCitizen(citizen);
            pernik.addCitizenAndID(citizen);
            post.addToPersonTypesAndIDs(citizen);
            citizen.start();
        }

        for (int i = 1; i <= 5; i++) {
            Postman postman = new Postman("Postman", String.valueOf(i));
            LetterPicker letterPicker = new LetterPicker("LetterPicker", String.valueOf(i));
            post.addPostman(postman);
            post.addPostman(letterPicker);
            postman.start();
            letterPicker.start();
        }

        Thread archiver = new Thread(new Runnable() {
            int fileNum = 1;
            @Override
            public void run() {
                while(true) {
                    post.writeShipmentsStoryToArchive(finalChoice);
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        archiver.setDaemon(true);
        archiver.start();

        Thread inquirer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    post.makeInquiry();
                }
            }
        });
        inquirer.setDaemon(true);
        inquirer.start();
    }
}