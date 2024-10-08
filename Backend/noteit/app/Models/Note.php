<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Note extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id' ,
        'title' , 
        'content'
    ] ;


    // Each note belongs to user
    public function user() : BelongsTo {
        return $this->belongsTo(User::class , 'user_id') ;
    }
}
