<?php

use App\Http\Controllers\NoteController;
use App\Http\Controllers\UserController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

//==============================================
//                 Users
//==============================================
Route::post('/users/register' , [UserController::class , 'register']) ; 

Route::post('/users/login' , [UserController::class , 'login']) ; 


//==============================================
//                 Notes
//==============================================
Route::middleware('auth:sanctum')->group(function(){

    Route::get('/notes' , [NoteController::class , 'index']) ; 

    Route::get('/notes/{note}' , [NoteController::class , 'show']) ; 

    Route::post('/notes/create' , [NoteController::class , 'create']) ; 

    Route::post('/notes/{note}' , [NoteController::class , 'update'])  ;

    Route::delete('/notes/{note}' , [NoteController::class , 'delete'])  ;

}) ; 