const mongoose = require('mongoose');
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

module.exports = mongoose.model('map', MapSchema);
