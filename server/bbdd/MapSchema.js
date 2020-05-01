const mongoose = require('mongoose');
const idValidator = require('mongoose-id-validator');
const location = require('../auxiliary/geoJSON');
const Schema = mongoose.Schema;

const MapSchema = new Schema({
    name: {
        type: String, required: true, unique: true,
        minlength: process.env.MAP_NAME_MIN_LENGTH,
        maxlength: process.env.MAP_NAME_MAX_LENGTH
    },
    owner: { type: Schema.Types.ObjectId, ref: 'user', required: true },
    public: { type: Boolean, default: true },
    firstLocation: { type: location, required: true },
    date: { type: Date, required: true, default: Date.now },
    metadata: {
        author: { type: String, maxlength: 15 },
        description: {
            type: String,
            minlength: process.env.MAP_DESCRIPTION_MIN_LENGTH,
            maxlength: process.env.MAP_DESCRIPTION_MAX_LENGTH
        }
    },
    points: [{ type: Schema.Types.ObjectId, ref: 'point' }],
});

MapSchema.plugin(idValidator, {
    allowDuplicates: true
});
MapSchema.index({ firstLocation: "2dsphere" });
module.exports = mongoose.model('map', MapSchema);
