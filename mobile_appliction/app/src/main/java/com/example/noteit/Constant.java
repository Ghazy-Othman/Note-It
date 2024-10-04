package com.example.noteit;

import java.util.ArrayList;
import java.util.List;

public class Constant {



    static String ip = "" ;

    static  public  final String userRegister = ip +"api/users/register" ;
    static  public  final String userLogin = ip + "api/users/login" ;
    static  public  final String getUserNotes = ip + "api/notes" ;
    static  public  final String addNote = ip + "api/notes/create" ;
    static  public  final String updateNote = ip + "api/notes/" ;

    static  List<Note> user_notes = new ArrayList<Note>();




}
