<script lang="ts">
  import { IconSend, IconMoodSearch } from "@tabler/icons-svelte";

  import { over } from "stompjs";
  import SockJS from "sockjs-client";
  import Message from "./lib/Message.svelte";
  import Avatar from "./lib/Avatar.svelte";

  let stompClient = null;
  let userData = {
    username: "",
    message: "",
    connected: false,
  };

  let publicChats = [];
  let privateChats = new Map();
  let tab = "CHATROOM";

  const connect = () => {
    let Sock = new SockJS("http://localhost:8080/ws");
    stompClient = over(Sock);
    stompClient.connect({}, onConnected, onError);
  };

  const onConnected = () => {
    userData = { ...userData, connected: true };
    stompClient.subscribe("/chatroom/public", onMessageReceived);
    stompClient.subscribe(
      "/user/" + userData.username + "/private",
      onPrivateMessage
    );
    userJoin();
  };

  const userJoin = () => {
    let chatMessage = {
      from: userData.username,
      status: "JOIN",
    };
    stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
  };

  const onMessageReceived = (payload) => {
    let payloadData = JSON.parse(payload.body);
    switch (payloadData.status) {
      case "JOIN":
        if (!privateChats.get(payloadData.from)) {
          privateChats.set(payloadData.from, []);
          privateChats = new Map(privateChats);
        }
        break;
      case "MESSAGE":
        publicChats.push(payloadData);
        publicChats = [...publicChats];

        break;
    }
  };

  const onPrivateMessage = (payload) => {
    console.log(payload);
    let payloadData = JSON.parse(payload.body);
    if (privateChats.get(payloadData.from)) {
      privateChats.get(payloadData.from).push(payloadData);
      privateChats = new Map(privateChats);
    } else {
      let list = [];
      list.push(payloadData);
      privateChats.set(payloadData.from, list);
      privateChats = new Map(privateChats);
    }
  };

  const onError = (err) => {
    console.log(err);
  };

  const handleMessage = (event) => {
    const { value } = event.target;
    userData = { ...userData, message: value };
  };

  const sendValue = () => {
    if (userData.message.trim().length === 0) return;
    if (stompClient) {
      let chatMessage = {
        from: userData.username,
        message: userData.message,
        status: "MESSAGE",
      };
      console.log(chatMessage);
      stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
      userData = { ...userData, message: "" };
    }
  };

  const sendPrivateValue = () => {
    if (userData.message.trim().length === 0) return;
    if (stompClient) {
      let chatMessage = {
        from: userData.username,
        to: tab,
        message: userData.message,
        status: "MESSAGE",
      };

      if (userData.username !== tab) {
        privateChats.get(tab).push(chatMessage);
        privateChats = new Map(privateChats);
      }
      stompClient.send("/app/private-message", {}, JSON.stringify(chatMessage));
      userData = { ...userData, message: "" };
    }
  };

  const handleUsername = (event) => {
    const { value } = event.target;

    userData = { ...userData, username: value };
  };

  const registerUser = () => {
    connect();
  };
</script>

<div
  class="w:full mx:auto height:100vh overflow:auto flex jc:center ai:center font-size:14px"
>
  {#if userData.connected}
    <div
      class="grid h:full w:full grid-template-columns:repeat(8,minmax(0,1fr))"
    >
      <section
        class="w:full p:.5rem bg:linear-gradient(violet-90,indigo-90) grid-col-span:2 border-right:1px|solid|gray-84"
      >
        <header class="my:1rem text-align:center">
          <h2>Talk to your friends</h2>
        </header>

        <label class="block my:2rem position:relative">
          <input
            type="text"
            placeholder="find friends"
            class="w:full p:.5rem border-radius:.5rem bg:white b:1px|solid|transparent border-color:violet-70:focus outline:none"
          />
          <span
            class="position:absolute h:full aspect-ratio:1 p:.5rem right:0 top:0"
          >
            <IconMoodSearch />
          </span>
        </label>

        <ul class="flex flex-direction:col gap:.5rem">
          <button
            on:click={() => {
              tab = "CHATROOM";
            }}
            class="flex ai:center w:full gap:.5rem p:.5rem rounded-md cursor:pointer bg:white:hove border-radius:.75rem outline:none b:1.5px|solid|transparent border-color:gray-86:focus border-color:gray-86:hover"
            class:bg:white={tab === "CHATROOM"}
          >
            <Avatar alt="Chatroom" rounded />
            <span>Chatroom</span>
          </button>

          {#each [...privateChats.keys()] as name, index (index)}
            <button
              on:click={(event) => {
                tab = name;
              }}
              class="flex ai:center w:full gap:.5rem p:.5rem rounded-md cursor:pointer bg:white:hove border-radius:.75rem outline:none b:1.5px|solid|transparent border-color:gray-86:focus border-color:gray-86:hover pointer-events:none:disabled d:none:disabled"
              class:bg:white={tab === name}
              disabled={name === userData.username}
            >
              <Avatar alt={name} rounded />
              <span>
                {name}
              </span>
            </button>
          {/each}
        </ul>
      </section>

      {#if tab === "CHATROOM"}
        <aside class="grid-col-span:6 d:flex flex:col overflow:hidden">
          <header
            class="d:flex align-items:center gap:1rem p:.75rem bg:violet-90"
          >
            <Avatar alt="CHATROOM" rounded />
            <div class="d:flex flex:col">
              <span class="font-weight:600">CHATROOM</span>
              <span class="font-size:12px color:gray-56">
                Lorem ipsum dolor sit amet
              </span>
            </div>
          </header>

          <ul class="flex-grow:1 overflow-y:scroll p:.5rem">
            {#each publicChats as chat, index (index)}
              <Message
                username={chat.from}
                message={chat.message}
                self={chat.from === userData.username}
              />
              <!-- avatar="https://i.pinimg.com/236x/9b/38/e6/9b38e65e039d11b3323a2cfb4106fd52.jpg" -->
            {/each}
          </ul>

          <form
            on:submit|preventDefault={sendValue}
            class="flex gap:.5rem position:relative p:1.25rem|.75rem bg:violet-90"
          >
            <input
              type="text"
              placeholder="enter the message"
              class="p:.5rem border-radius:.75rem w:full bg:white b:1px|solid|transparent border-color:violet-70:focus outline:none"
              value={userData.message}
              on:change={handleMessage}
            />
            <button
              class="aspect-ratio:1 p:.5rem bg:linear-gradient(violet-70,indigo-70) color:white border-radius:10rem align-self:center"
            >
              <IconSend />
            </button>
          </form>
        </aside>
      {/if}

      {#if tab !== "CHATROOM"}
        <aside class="grid-col-span:6 d:flex flex:col overflow:hidden">
          <header
            class="d:flex align-items:center gap:1rem p:.75rem bg:violet-90"
          >
            <Avatar alt={tab} rounded />

            <div class="d:flex flex:col">
              <span class="font-weight:600">{tab}</span>
              <span class="font-size:12px color:gray-56">
                Lorem ipsum dolor sit amet
              </span>
            </div>
          </header>

          <ul class="flex-grow:1 overflow-y:scroll p:.5rem">
            {#each [...privateChats.get(tab)] as chat, index (index)}
              <Message
                username={chat.from}
                message={chat.message}
                self={chat.from === userData.username}
                groupMessages
              />
            {/each}
          </ul>

          <form
            on:submit|preventDefault={sendPrivateValue}
            class="flex gap:.5rem position:relative p:1.25rem|.75rem bg:violet-90"
          >
            <input
              type="text"
              placeholder="enter the message"
              class="p:.5rem border-radius:.75rem w:full bg:white b:1px|solid|transparent border-color:violet-70:focus outline:none"
              value={userData.message}
              on:change={handleMessage}
            />
            <button
              class="aspect-ratio:1 p:.5rem bg:linear-gradient(violet-70,indigo-70) color:white border-radius:10rem align-self:center"
            >
              <IconSend />
            </button>
          </form>
        </aside>
      {/if}
    </div>
  {:else}
    <form
      on:submit|preventDefault
      class="flex gap:.5rem position:relative p:.5rem bg:violet-90 border-radius:.75rem"
    >
      <input
        id="user-name"
        placeholder="Your username"
        name="userName"
        class="p:.5rem border-radius:.5rem bg:white b:1px|solid|transparent border-color:violet-70:focus outline:none"
        value={userData.username}
        on:change={handleUsername}
      />
      <button
        class="p:.5rem|1rem border-radius:.55rem bg:violet color:white"
        on:click={registerUser}
      >
        connect
      </button>
    </form>
  {/if}
</div>
