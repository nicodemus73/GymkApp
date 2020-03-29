const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const PointSchema = new Schema({
    name:           { type: String,  required: true, unique: true, maxlength: 15 },
    owner:          { type: String,  required: true, maxlength: 15 }, 
    type:           { type: String,  required: true, maxlength: 4 },
    description:    { type: String,  required: true, maxlength: 150 },
  //  answers:         [{ type: String,  required: true, maxlength: 15 }],
    public:         { type: Boolean, default:  true },
    coord: {
        lat:  { type: mongoose.Decimal128,  required: true },
        long: { type: mongoose.Decimal128,  required: true }
    }
});

module.exports = mongoose.model('point', PointSchema);