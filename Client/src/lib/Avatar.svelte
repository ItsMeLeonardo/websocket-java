<script lang="ts">
  export let src: string | undefined = undefined;
  export let alt: string;
  export let size: "sm" | "md" | "lg" = "sm";
  export let rounded: boolean | "full" = false;
  export let clickable: boolean = false;
</script>

<picture
  class="aspect-ratio:1 flex justify-content:center align-items:center bg:violet-88 color:violet-40 font-weight:bold"
  class:border-radius:.5rem={rounded === true}
  class:round={rounded === "full"}
  class:w:40px={size === "sm"}
  class:w:48px={size === "md"}
  class:w:56px={size === "lg"}
  class:cursor-pointer={clickable}
  role={clickable ? "button" : undefined}
>
  {#if src}
    <img
      class="object-fit:cover w:full h:full border-radius:inherit"
      {src}
      {alt}
    />
    <!-- content here -->
  {:else}
    <span class="font-size:12 uppercase">
      {alt.slice(0, 2).toUpperCase()}
    </span>
  {/if}
</picture>
