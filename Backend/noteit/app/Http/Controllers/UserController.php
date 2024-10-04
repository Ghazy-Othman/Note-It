<?php

namespace App\Http\Controllers;

use App\Http\Requests\UserLoginRequest;
use App\Http\Requests\UserRegisterRequest;
use App\Http\Resources\UserResource;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;


/**
 * @group User
 * 
 */
class UserController extends Controller
{
    /**
     * User Register
     * 
     * @bodyParam name string required User name . Example: Ghazy
     * @bodyParam email string required User email . Example: ghazy@gmail.com
     * @bodyParam password string required User password . min:5
     * 
     */
    public function register(UserRegisterRequest $request){


        $validation = $request->validated() ;

        $newUser = User::create([
            'name' => $validation['name'] , 
            'email' => $validation['email'] , 
            'password' => Hash::make($validation['password'])
        ]) ;
        
        $success['token'] = $newUser->createToken("USER")->plainTextToken ; 
        $success['success'] = true ;
        $success['msg'] = "Register successed" ;
        $success['user'] = new UserResource($newUser);


        return response()->json( $success , 200) ;

    }


    /**
     * User Login
     * 
     * @bodyParam email string required User email . Example: ghazy@gmail.com
     * @bodyParam password string required User password .
     * 
     */
    public function login(UserLoginRequest $request) {

        $validation = $request->validated() ;

        $cred = [
            'email' => $validation['email'] , 
            'password' => $validation['password']
        ]; 


        if(Auth::attempt($cred)){

                $user = User::find(Auth::user()->id);
                $user->tokens()->delete() ;
                $success['token'] = $user->createToken("USER")->plainTextToken ; 
                $success['success'] = true ; 
                $success['msg'] = "Login successed" ;
                $success['user'] = new UserResource($user) ;

                return response()->json( $success , 200) ; 
        }
        else {
            return response()->json( [
                'success' => false ,
                'error' => "Unauthorized"
            ], 401) ;
        }

    }
}
