<template>
<form @submit.prevent="submit">
  <h2 class="heading">Чернильная сова</h2>
  <p>
    На клочке бумаги написаны заветные слова... Осталось только шепнуть сове имя
    ученика, которого ждет приятный сюрприз:
  </p>
  <input class="input" type="text" v-model="receiver" placeholder="Имя ученика">
  <button class="button" type="submit">Отправить</button>
  <a href="#" class="action-link action-link--inline" @click.prevent="$emit('cancel')">
    Пожалуй, в другой раз
  </a>
</form>
</template>

<script>
export default {
  name: 'StudentLetter',
  data: () => ({
    receiver: ''
  }),
  methods: {
    submit() {
      this.$emit('submit', { receiver: this.receiver });
    }
  }
}
</script>
