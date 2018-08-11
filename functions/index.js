let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

var db = admin.database();
var ref = db.ref("/users/{userId}/dates/");

exports.sendNotification = functions.database.ref('/users/{userId}/dates/').onWrite((snapshot, context) => {
	// ///get the userId of the person receiving the notification because we need to get their token
	const receiverId = context.params.userId;
	console.log("receiverId: ", receiverId);
  const rootRef = admin.database().ref('/users/' + receiverId);

  var dates;

  rootRef.once('value').then(snap => {
    // snapshot.forEach(function(childSnapshot) {
    //   console.log(childSnapshot.key+": "+childSnapshot.val());
    // });
    dates = snap.child("dates").val();
    console.log('JSON.stringify',dates);
    return true;
  }).catch(function(error){
    console.error(error);
  });
  //console.log("First: " + Object.keys(dates)[0]);
  // var dates;
  // return admin.database().ref("/users/" + receiverId).once('value').then(function(snapshot) {
  //   dates = snaphot.child("dates").val();
  //   console.log("dates Node: ", dates);
  // }).catch(function(error) {
  //   console.error(error);
  // });

  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth()+1; //January is 0!
  var yyyy = today.getFullYear();

  today = mm + ' ' + dd + ' ' + yyyy;
  console.log(today);



  // const dates = context.ref.parent.child("Harvard").val();
  // console.log("dates: ", dates);
  // const receiverId = event.data.child('user_id').val();
	// console.log("receiverId: ", receiverId)

  // const username = snapshot.ref.parent.child('username').val();
  // console.log("username: ", username);

	// ///get the message id. We'll be sending this in the payload
	// const messageId = event.params.messageId;
	// console.log("messageId: ", messageId);
  //
	// 	///get the token of the user receiving the message
  return admin.database().ref("/users/" + receiverId).once('value').then(snap => {
    const custom = snap.child("dates").val();
    console.log("Node date: " + JSON.stringify(custom));

  		return admin.database().ref("/users/" + receiverId).once('value').then(snap => {
        const username = snap.child("username").val();
  			//console.log("token: ", token);
        console.log("user: ", username);

        return admin.database().ref("/users/" + receiverId).once('value').then(snap1 => {
          const token = snap1.child("messaging_token").val();
        // This registration token comes from the client FCM SDKs.
        //const registrationToken = 'c_DV0-f7imo:APA91bGGLfzuFXtEZWP35qd1Orw73EBE1D61r2JnUhGaCe4r0_YXXZvFueS30y8iO1wUXMhY3uH0GhanjX1gT86E_MGDVLXQLm1tWWpgiTr1fYpEgt8C1_sdtVPIQ918xJW9CHLdy-5Mu3veAyrNqFYIFSTWNCRA1Q';


            // var len = dates.length;
            // var i;
            // for (i = 0; i < len; i++) {
            //   var d = dates[Object.keys(dates)[i]];
            //   console.log("Date: " + d);
            // }

            for (const key of Object.keys(dates)) {
              var da = dates[key];
              console.log(key, da);
              for (const key1 of Object.keys(da)) {
                console.log(da[key1].replace(/ /g,'/'));
                var diff = Math.abs(new Date() - new Date(da[key1].replace(/ /g,'/')));
                var days, hours, minutes, seconds, total_hours, total_minutes, total_seconds;
                total_seconds = parseInt(Math.floor(diff / 1000));
                total_minutes = parseInt(Math.floor(total_seconds / 60));
                total_hours = parseInt(Math.floor(total_minutes / 60));
                days = parseInt(Math.floor(total_hours / 24));
                console.log(key, key1, da[key1], days);
                if(days< 3) {
                  const payload = {
                    // data: {
                    //   data_type: 'reminder',
                    //   title: "Hey " + username + "!",
                    //   message: "Deadline coming up",
                    //   //icon: 'https://github.com/Aw3someT3am/UniPath/blob/master/app/src/main/up_icon_design-web.png'
                    // },
                    notification: {
                      title: "Hey " + username + "!",
                      body: days + 1 + " days left for this upcoming deadline: " + key + " on " + key1
                    },
                  };

                  admin.messaging().sendToDevice(token, payload);
                }
              }
            }

            // var deadline = dates[Object.keys(dates)[1]];
            // var date = deadline[Object.keys(deadline)[0]];
            // console.log("Deadline: " + JSON.stringify(deadline));
            // console.log("Date of Deadline: "+ date);
            // var c, d;
            // for (college in custom) {
            //   for(date in college) {
            //     c = college;
            //     d = date;
            //     console.log("college: " + c + " Date: " + d);
            //   }
            // }

            return true;
          });

        });

      });
    });

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
