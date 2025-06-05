using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using Client.Scenes.NetComClasses;
using Godot;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Action = Client.Scenes.NetComClasses.Action;
using System.Threading;
using Thread = System.Threading.Thread;


namespace Client.Scenes.Autoloads
{
    public class ServerConnection : Node
    {
        private Observable currentHandler;
        private string ip;

        private string port;

        //private Messagehandler _messagehandler;
        private int id;
        private bool isActive;
        public string nickname;
        public GameCfg _gameCfg;
        private int pauseclientID;
        private Queue<string> incomingMessages = new Queue<string>();

        private int i = 0;

        private Godot.WebSocketClient ws;
        private string secret;

        // Called when the node enters the scene tree for the first time.
        public override void _Ready()
        {
            ws = new Godot.WebSocketClient();

            ws.Connect("data_received", this, "_on_data_received");
            ws.Connect("connection_established", this, "_on_connection_established");
            ws.Connect("connection_closed", this, "_on_connection_closed");
            ws.Connect("connection_error", this, "_on_connection_closed");
        }


        public void setSecret(string secret)
        {
            this.secret = secret;
        }


        public override void _Process(float delta)
        {
            //GD.Print(ws.GetConnectedHost());
            ws.Poll();
            if (incomingMessages.Count != 0)
                currentHandler.parseMessage(incomingMessages.Dequeue());
        }

        public void setpauseclientID(int id)
        {
            this.pauseclientID = id;
        }

        public int getpauseclientID()
        {
            return pauseclientID;
        }

        public int getOwnID()
        {
            return id;
        }

        public void setID(int id)
        {
            this.id = id;
        }

        public void setGameCfg(GameCfg cfg)
        {
            this._gameCfg = cfg;
        }

        public GameCfg getGameCfg()
        {
            return _gameCfg;
        }


        public int getID()
        {
            return id;
        }


        /**
		 * for the handler to set themselves as currenthandler
		 */
        public void setCurrentHandler(Observable handler)
        {
            this.currentHandler = handler;
        }

        public void setIP(string ip)
        {
            this.ip = ip;
        }

        public void setPort(string port)
        {
            this.port = port;
        }

        /**
 * called when the websocket receives messages
 */
        public void _on_data_received()
        {
            string data = ws.GetPeer(1).GetPacket().GetStringFromUTF8();
            incomingMessages.Enqueue(data);
            GD.Print(data.Substring(0, Math.Min(data.Length - 1, 200)) + " " + currentHandler.GetType());
        }

        /**
 * called automatically after establishing a new connection. Sends Join Message instantly.
 */
        public void _on_connection_established(string protocol)
        {
            _connected = true;
            GD.Print("connected with: " + ws.GetConnectedHost() + ":" + ws.GetConnectedPort());

            //set writemode from byte to textmode
            ws.GetPeer(1).SetWriteMode(WebSocketPeer.WriteMode.Text);

            Join joinMessage = new Join(nickname, isActive, false);

            GD.Print(JObject.FromObject(joinMessage).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(joinMessage).ToString().ToUTF8());
        }

        public void pauseRequest(bool pause)
        {
            PauseRequest request = new PauseRequest(pause);
            GD.Print(JObject.FromObject(request).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public void moveCharacter(int characterId, SpecsMovement specsMovement)
        {
            MovementRequest request = new MovementRequest(this.id, characterId, specsMovement);
            GD.Print(JObject.FromObject(request).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public void attackChracter(int characterId, Coords target)
        {
            ActionRequest request = new ActionRequest(id, characterId, Action.ATTACK, new SpecsAction(target));
            GD.Print(JObject.FromObject((request)).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public void transferSpice(int characterId, int targetID, int amount)
        {
            TransferRequest request = new TransferRequest(id, characterId, targetID, amount);
            GD.Print(JObject.FromObject((request)).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public void collectSpice(int characterId, Coords target)
        {
            ActionRequest request = new ActionRequest(id, characterId, Action.COLLECT, new SpecsAction(target));
            GD.Print(JObject.FromObject((request)).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }


        public void voiceAttack(int characterId, Coords target)
        {
            ActionRequest request = new ActionRequest(id, characterId, Action.VOICE, new SpecsAction(target));
            GD.Print(JObject.FromObject((request)).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public void familyAtomics(int characterID, Coords target)
        {
            ActionRequest request = new ActionRequest(id, characterID, Action.FAMILY_ATOMICS, new SpecsAction(target));
            GD.Print(JObject.FromObject((request)).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        //Family Spin Action
        public void spinToWin(int characterID, Coords target)
        {
            ActionRequest request = new ActionRequest(id, characterID, Action.SWORDSPIN, new SpecsAction(target));
            GD.Print(JObject.FromObject((request)).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public void spiceHoarding(int characterID, Coords target)
        {
            ActionRequest request = new ActionRequest(id, characterID, Action.SPICE_HOARDING, new SpecsAction(target));
            GD.Print(JObject.FromObject((request)).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public void kanlyAttack(int characterID, Coords target)
        {
            ActionRequest request = new ActionRequest(id, characterID, Action.KANLY, new SpecsAction(target));
            GD.Print(JObject.FromObject((request)).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }


        public void skipTurn(int characterID)
        {
            EndTurnRequest request = new EndTurnRequest(id, characterID);
            GD.Print(JObject.FromObject((request)).ToString());
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public void getGameState()
        {
            GameStateRequest request = new GameStateRequest(this.id);
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public Boolean joinGame(string nickname, string ip, string port, bool isActive)
        {
            this.isActive = isActive;
            this.nickname = nickname;
            Godot.Error connected = ws.ConnectToUrl("ws://" + ip + ":" + port);

            if (connected != Godot.Error.Ok)
            {
                return false;
            }
            else
            {
                return true;
            }
        }


        public void heliPort(int characterID, Coords target)
        {
            HeliRequest request = new HeliRequest(id, characterID, target);
            ws.GetPeer(1).PutPacket(JObject.FromObject(request).ToString().ToUTF8());
        }

        public void sendRejoin()
        {
            Rejoin demand = new Rejoin(secret);
            ws.GetPeer(1).PutPacket(JObject.FromObject(demand).ToString().ToUTF8());
        }

        private bool _connected = false;

        public void _on_connection_closed(bool was_clean_closed)
        {
            _connected = false;
            if (was_clean_closed)
            {
                _connection_closed_clean();
            }
            else
            {
                //TODO: Popup for disconnect
                _connection_closed_notClean();
                for (int i = 0; i < 20; i++)
                {
                    if (_connected == false)
                    {
                        Thread.Sleep(1000);

                        Thread t = new Thread(new ThreadStart(sendRejoin));
                    }
                    else
                    {
                        break;
                    }
                }
                //TODO: reconnect
            }
        }

        public void setConnected(bool connected)
        {
            this._connected = connected;
        }

        private void _connection_closed_clean()
        {
            GD.Print("connection closed clean");
        }

        private void _connection_closed_notClean()
        {
            GD.Print("Connection closed not clean");
        }

        public void sendMessage(string message)
        {
            ws.GetPeer(1).PutPacket(message.ToUTF8());
        }
    }
}