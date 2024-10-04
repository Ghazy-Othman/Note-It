<?php

namespace App\Http\Controllers;

use App\Http\Requests\AddNoteRequest;
use App\Http\Resources\NoteCollection;
use App\Http\Resources\NoteResource;
use App\Models\Note;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;


/**
 * @group Notes
 * 
 * 
 */
class NoteController extends Controller
{

    /**
     * Get notes
     * 
     * Get all notes for specific user . 
     * 
     *@authenticated
     */
    public function index()
    {
        $user = User::find(Auth::user()->id);
        $success["success"] = true ;
        $success["notes"] = $user->notes ;
        return response()->json($success , 200);
    }


    /**
     * Make new note
     * 
     * @bodyParam title string required Note title . Example: New Note
     * @bodyParam content string required Note content . Example:  Welcome to our app 
     */
    public function create(AddNoteRequest $request)
    {
        $validation = $request->validated();
        $user = User::find(Auth::user()->id);

        $note = Note::create(
            [
                'user_id' => $user->id,
                'title' => $validation['title'],
                'content' => $validation['content']
            ]
        );

        $success['success'] = true;
        $success['note'] =  new NoteResource($note);


        return response()->json($success, 200);
    }

    /**
     * Get specific note
     */
    public function show(Note $note )
    {
        $success['success'] = true ; 
        $success['note'] = new NoteResource($note) ;

        return response()->json( $success , 200) ;
    }

    /**
     * Update note
     * 
     * @bodyParam title string  Note title . Example: New Note
     * @bodyParam content string  Note content . Example:  Welcome to our app 
     */
    public function update(Request $request,Note $note )
    {
        if($request->title){
            $note->title = $request->title ;
        }
        if($request->content){
            $note->content = $request->content ;
        }

        $note->save() ;

        $success['success'] = true ; 
        $success['note'] = new NoteResource($note) ;

        return response()->json($success , 200);
    }

    /**
     * Delete note
     */
    public function delete(Note $note )
    {
        //
        $note->delete();
        return response()->json(
            [
                'success' => true,
                'msg' => "Note has been deleted"
            ],
            200
        );
    }
}
