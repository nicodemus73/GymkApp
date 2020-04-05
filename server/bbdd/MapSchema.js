const mongoose = require('mongoose');
const idValidator = require('mongoose-id-validator');
const Schema = mongoose.Schema;

const MapSchema = new Schema({
    name:   { type: String,  required: true, unique: true, 
            minlength: process.env.MAP_NAME_MIN_LENGTH, 
            maxlength: process.env.MAP_NAME_MAX_LENGTH },
    owner:  { type: String,  required: true, maxlength: 15 }, 
    public: { type: Boolean, default:  true },
    metadata: {
        author:      { type: String, maxlength: 15 },
        description: { type: String, 
                    minlength: process.env.MAP_DESCRIPTION_MIN_LENGTH, 
                    maxlength: process.env.MAP_DESCRIPTION_MAX_LENGTH }
    },
    points: [{ type: Schema.Types.ObjectId, ref: 'point' }],
});

MapSchema.plugin(idValidator, {
    allowDuplicates: true
});
module.exports = mongoose.model('map', MapSchema);
