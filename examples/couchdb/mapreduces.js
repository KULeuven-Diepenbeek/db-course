var oef1 = {
  map: function (doc) {
    if(doc.title && doc.title.toLowerCase().indexOf("project") >= 0) {
      emit(doc._id, doc);
    }
  }
};

// correct antwoord: 29
var oef2_mogelijkheid1 = {
  map: function (doc) {
    if(doc.title && doc.title.toLowerCase().indexOf("project") >= 0) {
      emit(doc._id, doc.explicit ? 1 : 0);
    }
  },
  reduce: _sum
};
// zonder re-reduce zou het resultaat [ [ 4, 3, 0, 2, 9], [ 3, 3, 1, 3, 1]] zijn
var oef2_mogelijkheid2 = {
  map: function (doc) {
    if(doc.title && doc.title.toLowerCase().indexOf("project") >= 0) {
      emit(doc._id, doc.explicit);
    }
  },
  reduce: function (keys, values, rereduce) {
    if (!rereduce) {
      // omdat we NIET in de map de juite nummers hebben meegegeven, hier converteren van bool naar nr
      return values.map(function(explicit) {
        return explicit ? 1 : 0
      }).reduce(function(a, b) {
        // tel nummers op
        return a + b
      })
    } else {
      // tel resultaten van nummers opnieuw op
      return sum(values);
    }
  }
};

// correct antwoord: 1375
var oef3_manier1 = {
  map: function(doc) {
    // dit is verplicht, anders geeft CouchDB een fout dat hij in de _sum reduce NULL niet kan optellen
    // immers, sommige cursussen in de DB hebben geen 'ECTS property.
    emit(doc._id, doc.ECTS ? doc.ECTS : 0);
  },
  reduce: _sum
};
var oef3_manier2 = {
  map: function(doc) {
    emit(doc._id, doc.ECTS);
  },
  reduce: function (keys, values, rereduce) {
    return sum(values);
  }
};
var oef3_manier3 = {
  map: function(doc) {
    emit(doc._id, doc.ECTS);
  },
  reduce: function (keys, values, rereduce) {
    return values.reduce(function(a, b) {
      return a + b
    })
  }
};

