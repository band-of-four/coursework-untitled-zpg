<template>
<time :datetime="since" :title="formattedDate">{{ timeago }}</time>
</template>

<script>
const UPDATE_INTERVAL_MS = 30 * 1000;

export default {
  props: {
    since: { required: true }
  },
  data() {
    return {
      sinceDate: new Date(this.since),
      nowTime: new Date().getTime()
    }
  },
  computed: {
    timeago() {
      const seconds = Math.round(this.nowTime / 1000 - this.sinceDate.getTime() / 1000),
            minutes = Math.round(seconds / 60),
            hours = Math.round(minutes / 60);

      return seconds <= 5 ? 'только что'
           : seconds < 30 ? 'меньше полминуты назад'
           : seconds < 60 ? 'меньше минуты назад'
           : minutes == 1 ? 'минуту назад'
           : minutes <= 4 ? `${minutes} минуты назад`
           : minutes < 60 && minutes > 20 && minutes % 10 == 1 ? `${minutes} минута назад`
           : minutes < 60 && minutes > 20 && minutes % 10 > 0 && minutes % 10 < 5 ? `${minutes} минуты назад`
           : minutes < 60 ? `${minutes} минут назад`
           : hours == 1 ? 'час назад'
           : hours <= 4 ? `${hours} часа назад`
           : hours < 24 && hours > 20 && hours % 10 == 1 ? `${hours} час назад`
           : hours < 24 && hours > 20 && hours % 10 > 0 && hours % 10 < 5 ? `${hours} часа назад`
           : hours < 24 ? `${hours} часов назад`
           : this.formattedDate;
    },
    formattedDate() {
      return this.sinceDate.toLocaleDateString('ru-RU',
        { year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' });
    }
  },
  mounted() {
    this.interval = setInterval(() => { this.nowTime = new Date().getTime() }, UPDATE_INTERVAL_MS);
  },
  beforeDestroy() {
    clearInterval(this.interval);
  }
}
</script>
