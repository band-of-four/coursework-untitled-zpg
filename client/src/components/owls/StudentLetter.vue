<template>
<form @submit.prevent="submit">
  <h3>Чернильная сова</h3>
  <p>
    На клочке бумаги написаны заветные слова... Осталось только шепнуть сове имя
    ученика, которого ждет приятный сюрприз:
  </p>
  <input type="text" v-model="receiver" placeholder="Имя ученика">
  <button type="submit">Отправить</button>
  <button @click.prevent="$emit('cancel')">Пожалуй, в другой раз</button>
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
