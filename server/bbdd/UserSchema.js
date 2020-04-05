const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const userSchema = new Schema({
    username: { 
        type: String, 
        unique: true,
        min: 3,
        max: 255, 
        required: true 
    },
    password: { 
        type: String,
        min: 3,
        max: 1024,
        required: true 
    },
    /*firstname: { 
        type: String, 
        min: 2,
        max: 255,
        required: true 
    },
    lastname: { 
        type: String, 
        min: 2,
        max: 255,
        required: true 
    },*/
    games: [{
        type: Schema.Types.ObjectId,
        ref: 'map'
    }],
    /*friends: [{
         type: Schema.Types.ObjectId, 
         ref: 'Friends'
        }],*/

    createdDate: { 
        type: Date, 
        default: Date.now  //es pot modificar
    }


});

module.exports = mongoose.model('Usuario', userSchema); //toma el schema y lo guarda en una coleccion de mongodb