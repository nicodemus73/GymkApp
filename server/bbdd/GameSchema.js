const mongoose = require('mongoose');
const idValidator = require('mongoose-id-validator');
const location = require('../auxiliary/geoJSON');
const Schema = mongoose.Schema;

const GameSchema = new Schema({

    user: { type: Schema.Types.ObjectId, ref: 'user', required: true },
    map: { type: Schema.Types.ObjectId, ref: 'map', required: true },
    startDate: { type: Date, required: true, default: Date.now },
    startLocation: { type: location, required: true },
    status: { type: String, required: true, default: 'inProgress', enum: ['inProgress', 'completed', 'abandoned'] },
    progress: [{
        point: { type: Schema.Types.ObjectId, ref: 'point', required: true },
        tries: { type: Number, min: 1, required: true, default: 1 },
        completedDate: { type: Date },
    }],
    endDate: { type: Date },
    stats: {
        punctuation: { type: Number, min: 1, max: 5 },
        comment: {
            type: String,
            minlength: process.env.GAME_COMMENT_MIN_LENGTH,
            maxlength: process.env.GAME_COMMENT_MAX_LENGTH
        }
    }
});

GameSchema.plugin(idValidator, {
    allowDuplicates: true
});
GameSchema.index({ startLocation: "2dsphere" });
module.exports = mongoose.model('game', GameSchema);