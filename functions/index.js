let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

var db = admin.database();
var ref = db.ref("/users/{userId}/dates/");

exports.sendNotification = functions.database.ref('/users/{userId}/dates/').onWrite((snapshot, context) => {
  var rootRef = admin.database().ref('/users/{userId}/dates/');
	// ///get the userId of the person receiving the notification because we need to get their token
	const receiverId = context.params.userId;
	console.log("receiverId: ", receiverId);

  var dates;

  rootRef.once('value').then(function(snapshot) {
    // snapshot.forEach(function(childSnapshot) {
    //   console.log(childSnapshot.key+": "+childSnapshot.val());
    // });
    dates = snapshot.val();
    console.log("Node date: " + dates);
    return true;
  }).catch(function(error){
    console.error(error);
  });

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

  today = mm + '/' + dd + '/' + yyyy;
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

  		return admin.database().ref("/users/" + receiverId).once('value').then(snap => {
        const username = snap.child("username").val();
  			//console.log("token: ", token);
        console.log("user: ", username);

        return admin.database().ref("/users/" + receiverId).once('value').then(snap1 => {
          const token = snap1.child("messaging_token").val();
        // This registration token comes from the client FCM SDKs.
        //const registrationToken = 'c_DV0-f7imo:APA91bGGLfzuFXtEZWP35qd1Orw73EBE1D61r2JnUhGaCe4r0_YXXZvFueS30y8iO1wUXMhY3uH0GhanjX1gT86E_MGDVLXQLm1tWWpgiTr1fYpEgt8C1_sdtVPIQ918xJW9CHLdy-5Mu3veAyrNqFYIFSTWNCRA1Q';

            // var message = {
            //   android: {
            //     ttl: 3600 * 1000, // 1 hour in milliseconds
            //     priority: 'normal',
            //     notification: {
            //       title: "Hey " + username + "!",
            //       body: "Deadline coming up",
            //       icon: 'https://github.com/Aw3someT3am/UniPath/blob/master/app/src/main/up_icon_design-web.png',
            //       //color: '#f45342'
            //     }
            //   },
            //   topic: 'industry-tech'
            // };

            const payload = {
              // data: {
              //   data_type: 'reminder',
              //   title: "Hey " + username + "!",
              //   message: "Deadline coming up",
              //   //icon: 'https://github.com/Aw3someT3am/UniPath/blob/master/app/src/main/up_icon_design-web.png'
              // },
              notification: {
                title: "Hey " + username + "!",
                body: "Deadline coming up: " + custom
              },
            };

            return admin.messaging().sendToDevice(token, payload);
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
