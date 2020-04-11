const mongoose = require('mongoose');
const idValidator = require('mongoose-id-validator');
const Schema = mongoose.Schema;

const PointSchema = new Schema({
    name:           { type: String,  required: true, unique: true, 
                      minlength: process.env.POINT_NAME_MIN_LENGTH, 
                      maxlength: process.env.POINT_NAME_MAX_LENGTH },
    owner:          { type: Schema.Types.ObjectId, ref: 'user', required: true}, 
    type:           { type: String,  required: true, enum: ['foot', 'bike'] },
    description:    { type: String,  required: true, 
                      minlength: process.env.POINT_DESCRIPTION_MIN_LENGTH, 
                      maxlength: process.env.POINT_DESCRIPTION_MAX_LENGTH },
  //  answers:         [{ type: String,  required: true, maxlength: 15 }],
    public:         { type: Boolean, default:  true },
    coord: {
        lat:  { type: mongoose.Decimal128,  required: true },
        long: { type: mongoose.Decimal128,  required: true }
    }
});

PointSchema.plugin(idValidator, {
    allowDuplicates: true
});
module.exports = mongoose.model('point', PointSchema);
