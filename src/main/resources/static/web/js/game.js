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
            createHitsTable(gameData, "user");
            createHitsTable(gameData, "opponent");
            if(gameData.ships != 0) {
                $("#game-display, #history-display, #players").show();
                $("#place-ships").hide();
                printShipLocations(gameData);
            }
            userVsOpponent(gameData);
            if (gameData.gamePlayers.length == 2) {
                getUser(gameData);
                getOpponentId(gameData);
                printSalvoes(gameData, "my");
                printSalvoes(gameData, "op");
                hitsHistory(gameData, myId);
                hitsHistory(gameData, opId);
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
            hitsHistory(gameData, myId);
            hitsHistory(gameData, opId);
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
                if (gameData.hitsAndSinks[turn][id]["hits"].hasOwnProperty(shot)) { //cambiar if por gamedata.Hitsandsinks.turn.id.hasOwnProperty(shot)
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

function createHitsTable(gameData, user) {
    console.log(gameData);
    var ships = gameData.ships;
    console.log(ships);
    ships.sort(function(a,b) {return (a.location.length > b.location.length) ? 1 : ((b.location.length > a.location.length) ? -1 : 0);} );
    for (ship in ships) {
        console.log(ship);
        var tr = document.createElement("tr");

        var td = document.createElement("td");
        if (ships[ship].type == "Patrol Boat") {
            $(td).attr("id", user + "PB");
        } else {
            $(td).attr("id", user + ships[ship].type);
        }
        $(td).addClass("type_history");
        td.innerHTML = ships[ship].type;
        tr.append(td);

        var shipLength = ships[ship].location.length;
        var td2 = document.createElement("td");
        for (var i = 0; i < shipLength; i++) {
            var div = document.createElement("div");
            $(div).addClass("hits_history");
            var type = ships[ship].type;
            if (type == "Patrol Boat") {
                type = "PB";
            }
            $(div).attr("id", user + "HitHistory" + type + (i+1));
            td2.append(div);
            tr.append(td2);
        }
        $("#" + user + "-hits-table > tbody").append(tr);
    }
}

function hitsHistory(data, id) {
    if (id == myId) {
        var table = "opponent";
    }else {
        var table = "user";
    }

    var maxDamage = {
        PB: 2,
        Submarine: 3,
        Destroyer: 3,
        Battleship: 4,
        Carrier: 5
    }

    var turn = 1;
    
    for (var key in data.hitsAndSinks) {
        if (key > turn) {
            turn = key;
        }
    }

    console.log(turn);
    
    if (data.hitsAndSinks.hasOwnProperty(turn) && data.hitsAndSinks[turn].hasOwnProperty(id)) {

            $(".hits_history").removeClass("damage_history");
            Object.keys(data.hitsAndSinks[turn][id].damages).forEach(function (key) {

                switch (key) {
                    case "Patrol Boat":
                        for (var i = 1; i <= data.hitsAndSinks[turn][id].damages[key]; i++) {
                            $("#" + table + "HitHistory" + "PB" + i).attr("class", "damage_history");
                            console.log(key);
                            console.log(i);
                            console.log("#" + table + "HitHistory" + "PB" + i);
                            if (i == maxDamage.PB) {
                                $("#" + table + "PB").addClass("line_through");
                            }
                        }
                        break;

                    default:
                        for (var i = 1; i <= data.hitsAndSinks[turn][id].damages[key]; i++) {
                            $("#" + table + "HitHistory" + key + i).attr("class", "damage_history");
                            console.log(key);
                            console.log(i);
                            console.log("#" + table + "HitHistory" + key + i);
                            if (i == maxDamage[key]) {
                                console.log(1, maxDamage[key]);
                                $("#" + table + key).addClass("line_through");
                            }
                        }
                        break;
                }
            })

        } else if (turn != 1) {
            $(".hits_history").removeClass("damage_history");
            Object.keys(data.hitsAndSinks[turn - 1][id].damages).forEach(function (key) {

                switch (key) {
                    case "Patrol Boat":
                        for (var i = 1; i <= data.hitsAndSinks[turn - 1][id].damages[key]; i++) {
                            $("#" + table + "HitHistory" + "PB" + i).attr("class", "damage_history");
                            console.log(key);
                            console.log(i);
                            console.log("#" + table + "HitHistory" + "PB" + i);
                            if (i == maxDamage.PB) {
                                $("#" + table + "PB").addClass("line_through");
                            }
                        }
                        break;

                    default:
                        for (var i = 1; i <= data.hitsAndSinks[turn - 1][id].damages[key]; i++) {
                            $("#" + table + "HitHistory" + key + i).attr("class", "damage_history");
                            console.log(key);
                            console.log(i);
                            console.log("#" + table + "HitHistory" + key + i);
                            if (i == maxDamage[key]) {
                                $("#" + table + key).addClass("line_through");
                            }
                        }
                        break;
                }
            })
        }

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