const mongoose = require('mongoose');
const idValidator = require('mongoose-id-validator');
const Schema = mongoose.Schema;

const MapSchema = new Schema({
    name:   { type: String,  required: true, unique: true, maxlength: 15 },
    owner:  { type: String,  required: true, maxlength: 15 }, 
    public: { type: Boolean, default:  true },
    metadata: {
        author:      { type: String, maxlength: 15 },
        description: { type: String, maxlength: 150 }
    },
    points: [{ type: Schema.Types.ObjectId, ref: 'point' }],
});

MapSchema.plugin(idValidator, {
    allowDuplicates: true
});
module.exports = mongoose.model('map', MapSchema);
