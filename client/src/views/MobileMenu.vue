<template>
<main class="mobile-menu-page">
  <div class="menu-item-container">
    <div class="menu-item">
      <div class="menu-item__icon icon--book"></div>
      <router-link class="menu-item__text" to="/diary">Дневник</router-link>
    </div>
    <div class="menu-item">
      <div class="menu-item__icon icon--owl"></div>
      <router-link class="menu-item__text" to="/owls">Совы</router-link>
    </div>
    <div class="menu-item">
      <div class="menu-item__icon icon--flasks"></div>
      <router-link class="menu-item__text" to="/relationships">Отношения</router-link>
    </div>
    <div class="menu-item">
      <div class="menu-item__icon icon--stars"></div>
      <router-link class="menu-item__text" to="/skills">Навыки</router-link>
    </div>
    <div class="menu-item">
      <div class="menu-item__icon icon--ball"></div>
      <router-link class="menu-item__text" to="/suggest">Предложить</router-link>
    </div>
    <div class="menu-item">
      <div class="menu-item__icon"></div>
      <a class="menu-item__text" href="/auth/signout">Выйти</a>
    </div>
  </div>
</main>
</template>

<script>
export default {
  name: 'MobileMenu'
}
</script>
