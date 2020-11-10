var oef23_1 = {
   "selector": {
      "ECTS": {
         "$gt": 5
      }
   }
}

var oef23_2 = "Zie https://docs.couchdb.org/en/stable/api/database/find.html - POST op /db/_find uitvoeren"


var oef_23_3 = {
   "selector": {
      "$and": [
         {
            "skills": {
               "$in": [
                  "self-reflection"
               ]
            }
         },
         {
            "skills": {
               "$in": [
                  "show initiative"
               ]
            }
         }
      ]
   }
}

var oef23_3_alt = {
   "selector": {
      "$and": [
         {
            "skills": {
               "$elemMatch": {
                  "$eq": "self-reflection"
               }
            }
         },
         {
            "skills": {
               "$elemMatch": {
                  "$eq": "show initiative"
               }
            }
         }
      ]
   }
}