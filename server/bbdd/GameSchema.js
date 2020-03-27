const mongoose = require('mongoose');
const idValidator = require('mongoose-id-validator');
const Schema = mongoose.Schema;

const GameSchema = new Schema({

    user: { type: String,  required: true, maxlength: 15 },
    map:{ type: Schema.Types.ObjectId, ref: 'map', required: true },
    startDate: { type: Date, required: true, default: Date.now},
    status: { type: String},
    //current:
    stats: {
        endDate: Date,
        punctuation: { type: Number, min: 1, max: 5},
        comment:{ type: String, maxlength: 150 }
    }
});

GameSchema.plugin(idValidator, {
    allowDuplicates: true
});
module.exports = mongoose.model('game', MapSchema);