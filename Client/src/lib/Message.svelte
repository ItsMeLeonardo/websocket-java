<script lang="ts">
  import Avatar from "./Avatar.svelte";

  export let username: string;
  export let avatar: string | undefined = undefined;
  export let message: string;
  export let self: boolean = false;
  export let groupMessages: boolean = false;

  const classes = {
    container: `message_container flex gap:.5rem align-items:center justify-content:center ${
      self && "flex:row-reverse"
    }
    ${!groupMessages && "mt:.5rem"}
    `,
    messageHeader: `message_header flex align-items:center gap:.5rem my:.25rem ${
      self && "flex:row-reverse"
    }
    `,
    message: `border-radius:2rem message_text font-size:.85rem py:.75rem px:1rem max-w:70% w:max ${
      self
        ? "bg:linear-gradient(violet-60,indigo-60) color:white"
        : `bg:gray-88`
    }
    ${groupMessages && "mt:.125rem"}
      `,
    messageWrap: `flex gap:.5rem align-items:center ${
      self && "justify-content:end flex:row-reverse"
    }`,
  };
</script>

<div
  class={classes.container}
  data-sender={groupMessages ? (self ? "self" : "other") : undefined}
>
  <div class="message_avatar">
    <Avatar alt={username} src={avatar} rounded="full" size="sm" />
  </div>
  <div class="flex-grow:1">
    <header class={classes.messageHeader}>
      <h6 class="font-size:12px font-weight:bold px:.5rem">{username}</h6>
    </header>
    <div class={`${classes.messageWrap} d:initial:hover>.time`}>
      <p class={classes.message}>{message}</p>
      <span class="time d:none font-size:.75rem">15:20</span>
    </div>
  </div>
</div>

<style scoped>
  .message_container[data-sender="self"] .message_text {
    border-bottom-right-radius: 0.75rem;
  }

  .message_container[data-sender="other"] .message_text {
    border-bottom-left-radius: 0.75rem;
  }

  .message_container[data-sender="self"]
    + .message_container[data-sender="self"]
    .message_avatar,
  .message_container[data-sender="other"]
    + .message_container[data-sender="other"]
    .message_avatar {
    opacity: 0;
  }
  .message_container[data-sender="self"]
    + .message_container[data-sender="self"]
    .message_header,
  .message_container[data-sender="other"]
    + .message_container[data-sender="other"]
    .message_header {
    display: none;
  }

  .message_container[data-sender="self"]
    + .message_container[data-sender="self"]
    .message_text {
    border-bottom-right-radius: 0.75rem;
    border-top-right-radius: 0.75rem;
  }

  .message_container[data-sender="other"]
    + .message_container[data-sender="other"]
    .message_text {
    border-bottom-left-radius: 0.75rem;
    border-top-left-radius: 0.75rem;
  }

  .message_container[data-sender="other"]:has(
      + .message_container[data-sender="self"]
    )
    .message_text {
    border-bottom-left-radius: 2rem;
  }

  .message_container[data-sender="self"]:has(
      + .message_container[data-sender="other"]
    )
    .message_text {
    border-bottom-right-radius: 2rem;
  }

  .message_container[data-sender="self"]:last-child .message_text {
    border-bottom-right-radius: 2rem !important;
  }

  .message_container[data-sender="other"]:last-child .message_text {
    border-bottom-left-radius: 2rem !important;
  }
</style>
