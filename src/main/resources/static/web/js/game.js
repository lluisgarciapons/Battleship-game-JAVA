var myId;
var opId;
var user;

$(document).ready(function() {
    printMyTable("my");
    printMyTable("op");
    printMyTable("pl");
    shipListeners();
    salvoListeners();
    urlCall();
})

function printMyTable(who){

    for(var i = 0; i < 11; i++) {
        var tr = document.createElement("tr");
        tr.setAttribute("class", "trow");

        if (i == 0) {
            for (k = 0; k < 11; k++) {
                var th = document.createElement("th");
                th.setAttribute("class", "header_cell");
                if (k > 0){
                    th.innerHTML = k;
                }
                tr.append(th);
                var selectorHeader = "#" + who + "-table-headers";
                $(selectorHeader).append(tr);
            }
        } else {
            for(var j = 0; j < 11; j++){
                var td = document.createElement("td");
                td.setAttribute("class", "tcell");
                if (who == "pl") {
                    td.setAttribute("id", String.fromCharCode(64 + i) + j);
                } else {
                    td.setAttribute("id", String.fromCharCode(64 + i) + j + who);
                }
                tr.append(td);
                if (j == 0){
                    td.innerHTML = String.fromCharCode(64 + i);
                }
                var selectorBody = "#" + who + "-table-rows";
                $(selectorBody).append(tr);
            }
        }
    }
}

function urlCall() {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: '/api/game_view/' + getKey("gp"),

        success: function (json) {
            console.log(json);
            var gameData = json;

            myId = getKey("gp");
            if(gameData.ships != 0) {
                $("#game-display, #players").show();
                $("#place-ships").hide();
                printShipLocations(gameData);
            }
            userVsOpponent(gameData);
            if (gameData.gamePlayers.length == 2) {
                getUser(gameData);
                getOpponentId(gameData);
                printSalvoes(gameData, "my");
                printSalvoes(gameData, "op");
            }
        },
        error: function (json) {
            console.log(json);
            $("#unauthorized-error-alert").text(json.responseJSON.error);
            $("#unauthorized-modal").modal("show");
        }
    })
}

function updateData() {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: '/api/game_view/' + getKey("gp"),

        success: function (json) {
            console.log(json);
            var gameData = json;
            printShipLocations(gameData);
        }
    });
}

function getOpponentId(gameData) {
        if (gameData.gamePlayers[0].id != myId) {
            opId = (gameData.gamePlayers[0].id).toString();
        }else {
            opId = (gameData.gamePlayers[1].id).toString();
    }
}

function getKey(name) {
    var url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex.exec(url);
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function printShipLocations(gameData) {
    var ships = gameData.ships;
    console.log(ships);
    for (var ship in ships) {
        var type = ships[ship].type;
        for (var j = 0; j < ships[ship].location.length; j++) {
            var cell = ships[ship].location[j];
            console.log(cell);
            var selCell = "#" + cell + "my";
            $(selCell)[0].setAttribute("class", "placed_ship");
            $(selCell)[0].setAttribute("data-type", type);
        }
    }
}

function getUser(gameData) {
    if (gameData.gamePlayers[0].id == myId) {
        user = gameData.gamePlayers[0].player.username;
    } else {
        user = gameData.gamePlayers[1].player.username;
    }
    $("#hi-user").append(user);
}

function userVsOpponent(gameData) {
    var user;
    var opponent;

    if (gameData.gamePlayers.length == 1) {
     user = gameData.gamePlayers[0].player.username;
     opponent = "[WAITING FOR AN OPPONENT]";
    } else {
        if (gameData.gamePlayers[0].id == myId) {
            user = gameData.gamePlayers[0].player.username;
            opponent = gameData.gamePlayers[1].player.username;
        } else {
            user = gameData.gamePlayers[1].player.username;
            opponent = gameData.gamePlayers[0].player.username;
        }
    }

    $("#user").append(user);
    $("#opponent").append(opponent);
    console.log(user, opponent);
}

function printSalvoes(gameData, who) {
    var salvoes = gameData.salvoes;
    // console.log(salvoes);
    var id;
    var against;
    if (who == "my") {
        id = myId;
        against = "op";
    } else {
        id = opId;
        against = "my";
    }

    for (var turn in salvoes) {
        // console.log("turn: ", turn);
        if (salvoes[turn].hasOwnProperty(id)) {
            var shots = salvoes[turn][id];
            // console.log("shots: ", shots);

            for (var i = 0; i < shots.length; i++) {
                var shot = shots[i];
                var cell = "#" + shot + against;
                if (gameData.hitsAndSinks[turn][id].hasOwnProperty(shot)) { //cambiar if por gamedata.Hitsandsinks.turn.id.hasOwnProperty(shot)
                    $(cell).addClass("hit");
                } else {
                    $(cell).addClass("miss");
                }
                $(cell).text(turn);
            }
        }
    }
}

function closeErrorModal() {
   $("#unauthorized-window").modal("hide");
    window.history.back();
}

function postShips (ships) {
    $.post({url: "/api/games/players/" + getKey("gp") + "/ships",
        data: JSON.stringify( ships
        //     [{
        //     type: "destroyer",
        //     locations: ["H2", "H4", "H3"]
        // },
        // {
        //     type: "Submarine",
        //     locations: ["A1", "A2", "A3"]
        // }]
        ),
        dataType: "text",
        contentType: "application/json"
    })
        .done(function(json){
            console.log("good", json);
            })
        .fail(function(json) {
            console.log("bad", json);
        })
}

function postSalvoes (salvo) {
    $.post({url: "/api/games/players/" + getKey("gp") + "/salvoes",
    data: JSON.stringify(salvo),
    dataType: "text",
    contentType: "application/json"
    })
        .done(function(json) {
            console.log("good", json);
        })
        .fail(function(json) {
            console.log("bad", json);
        })
}