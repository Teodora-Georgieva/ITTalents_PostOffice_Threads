package post.utils;

public abstract class Validator {
    public static boolean isValidString(String s){
        return s != null && s.length() > 0;
    }

    public static boolean isValidID(int id){
        return id > 0;
    }
}