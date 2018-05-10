var myId;
var opId;

$(document).ready(function() {
    printMyTable("my");
    printMyTable("op");
    urlCall();
})

function printMyTable(who){

    for(var i = 0; i < 11; i++) {
        var tr = document.createElement("tr");
        tr.setAttribute("class", "row");

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
                td.setAttribute("class", "cell");
                td.setAttribute("id", String.fromCharCode(64 + i) + j + who);
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

            printShipLocations(gameData);
            userVsOpponent(gameData);
            myId = getKey("gp");
            if (gameData.gamePlayers.length < 2) {
                return false;
            }
            getOpponentId(gameData, myId);
            printSalvoes(gameData, "my");
            printSalvoes(gameData, "op");
        }
    })
}

function getOpponentId(gameData, myId) {
    for (var i = 0; i < gameData.gamePlayers.length; i++) {
        if (gameData.gamePlayers[i].id != myId) {
            opId = (gameData.gamePlayers[i].id).toString();
        }
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
        for (var j = 0; j < ships[ship].location.length; j++) {
            var cell = ships[ship].location[j];
            console.log(cell);
            var selCell = "#" + cell + "my";
            $(selCell)[0].setAttribute("class", "ship");
        }
    }
}

function userVsOpponent(gameData) {
    var user;
    var opponent;

    if (gameData.gamePlayers[0].id == myId) {
        user = gameData.gamePlayers[0].player.username;
        if (gameData.gamePlayers[1]){
            opponent = gameData.gamePlayers[1].player.username;
        } else {
            opponent = "[WAITING FOR AN OPPONENT]"
        }
    } else {
        user = gameData.gamePlayers[1].player.username;
        opponent = gameData.gamePlayers[0].player.username;
    }

    $("#players").append(user + " Vs. " + opponent);
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
        var shots = salvoes[turn][id];
        // console.log("shots: ", shots);

        for (var i = 0; i < shots.length; i++) {
            var shot = shots[i];
            var cell = "#" + shot + against;
            if ($(cell).hasClass("ship")) {
                $(cell).addClass("hit");
            } else {
                $(cell).addClass("miss");
            }
            $(cell).text(turn);
        }
    }
}