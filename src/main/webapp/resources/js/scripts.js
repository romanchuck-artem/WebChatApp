
var uniqueId = function() {
    var date = Date.now();
    var random = Math.random() * Math.random();

    return Math.floor(date * random).toString();
};

var theMessage = function(text,name, id) {
    return {
        msg:text,
        nick: name,
        id: id
    };
};

var appState = {
    mainUrl: 'chat',
    messageList:[],
    token: 'TE11EN'
};

function storeMessages(sendMessage, continueWith) {

    post(appState.mainUrl, JSON.stringify(sendMessage),
        function () {
        });
};


function restoreMessages(continueWith) {
    var url = appState.mainUrl + '?token=' + appState.token;
    get(url, function (responseText) {
        console.assert(responseText != null);
        var response = JSON.parse(responseText);
        createAllTasks(response.messages);

        continueWith && continueWith();
    });
};

function update(continueWith){
    var url = appState.mainUrl + '?token=' + appState.token;
    get(url, function (responseText) {
        console.assert(responseText != null);
        var response = JSON.parse(responseText);
        for(var i=0; i < response.messages.length; i++){
            addMessage(response.messages[i]);
        }

        continueWith && continueWith();
    });
    setTimeout(update, 1000);
}

function addMessage(msg){
    if (appState.messageList[msg.id] != null) {
    } else {
        var incoming = document.getElementById('incoming_messages');
        var p = document.createElement('p');
        p.className += 'message';
        p_msg = document.createElement('p');
        p_msg.className += 'text_msg';
        nick_space = document.createElement('span');
        nick_space.className += 'nick';
        p_buttons = document.createElement('span');
        p_buttons.className += 'buttons';
        p_buttons.setAttribute('style', 'float: right');
        p_msg.innerHTML = msg.msgText + "</br>";
        nick_space.innerHTML = msg.userName;
        var edit_text = document.createElement('button');

        appState.messageList.push(theMessage(msg.msgText, msg.userName, msg.id));

        edit_text.onclick = function () {
            message_area.value = message;
            end_edit = document.getElementById('end_edit');
            end_edit.style.display = 'inline';
            end_edit.onclick = function () {
                p_msg.childNodes[0].data = message_area.value;
                end_edit.style.display = 'none';
                message_area.value = clean_message;
            };
        };
        var edit_img = document.createElement('img');
        edit_img.src = "resources/js/images/edit_img.png";
        var delete_text = document.createElement('button');
        delete_text.onclick = function () {
            var parent = p.parentElement;
            parent.removeChild(p);
        };
        var delete_img = document.createElement('img');
        delete_img.src = "resources/js/images/delete_img.png";
        incoming.appendChild(p);
        p_buttons.appendChild(edit_text);
        p_buttons.appendChild(delete_text);
        p.appendChild(nick_space);
        p.appendChild(p_buttons);
        p.appendChild(p_msg);
        edit_text.appendChild(edit_img);
        delete_text.appendChild(delete_img);

    }
}


function run(){
    restoreMessages();
    update();
}

function createAllTasks(allTasks) {
    for(var i = 0; i < allTasks.length; i++) {
            var incoming = document.getElementById('incoming_messages');
            var p = document.createElement('p');
            p.className += 'message';
            p_msg = document.createElement('p');
            p_msg.className += 'text_msg';
            nick_space = document.createElement('span');
            nick_space.className += 'nick';
            p_buttons = document.createElement('span');
            p_buttons.className += 'buttons';
            p_buttons.setAttribute('style', 'float: right');
            p_msg.innerHTML = allTasks[i].msgText + "</br>";
            nick_space.innerHTML = allTasks[i].userName;
            var edit_text = document.createElement('button');

            appState.messageList.push(theMessage(allTasks[i].msgText, allTasks[i].userName, allTasks[i].id));

            edit_text.onclick = function () {
                message_area.value = message;
                end_edit = document.getElementById('end_edit');
                end_edit.style.display = 'inline';
                end_edit.onclick = function () {
                    p_msg.childNodes[0].data = message_area.value;
                    end_edit.style.display = 'none';
                    message_area.value = clean_message;
                };
            };
            var edit_img = document.createElement('img');
            edit_img.src = "resources/js/images/edit_img.png";
            var delete_text = document.createElement('button');
            delete_text.onclick = function () {
                var parent = p.parentElement;
                parent.removeChild(p);
            };
            var delete_img = document.createElement('img');
            delete_img.src = "resources/js/images/delete_img.png";
            incoming.appendChild(p);
            p_buttons.appendChild(edit_text);
            p_buttons.appendChild(delete_text);
            p.appendChild(nick_space);
            p.appendChild(p_buttons);
            p.appendChild(p_msg);
            edit_text.appendChild(edit_img);
            delete_text.appendChild(delete_img);

  }
}

var login, online = true;

function sent()
{
    if(online)
    {
        login = document.getElementById('name').value;
        if(login != ""){
            var message = document.getElementById('message_area').value;
            var message_area = document.getElementById('message_area'),
                clean_message = ' ';
            message_area.value = clean_message;
            storeMessages(theMessage(message, login, -1));
        }
        else{
            alert("Enter name");
        }
    }
}

function get(url, continueWith, continueWithError) {
    ajax('GET', url, null, continueWith, continueWithError);
};
function post(url, data, continueWith, continueWithError) {
    ajax('POST', url, data, continueWith, continueWithError);
};
function put(url, data, continueWith, continueWithError) {
    ajax('PUT', url, data, continueWith, continueWithError);
};
function del(url, data, continueWith, continueWithError) {
    ajax('DELETE', url, data, continueWith, continueWithError);
};
function isError(text) {
    if (text == "")
        return false;

    try {
        var obj = JSON.parse(text);
    } catch (ex) {
        return true;
    }

    return !!obj.error;
};
function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();

    continueWithError = continueWithError;
    xhr.open(method || 'GET', url, true);

    xhr.onload = function () {

        if (xhr.readyState != 4)
            return;

        if(xhr.status != 304) {

            if (xhr.status != 200) {
                continueWithError('Error on the server side, response ' + xhr.status);
                return;
            }

            if (isError(xhr.responseText)) {
                continueWithError('Error on the server side, response ' + xhr.responseText);
                return;
            }
        }

        continueWith(xhr.responseText);
    };

    xhr.ontimeout = function () {
        continueWithError('Server timed out !');
    }

    xhr.onerror = function (e) {
        var errMsg = 'Server connection error !\n' +
            '\n' +
            'Check if \n' +
            '- server is active\n' +
            '- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };

    xhr.send(data);
};

