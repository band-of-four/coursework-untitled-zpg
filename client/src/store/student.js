export default ({ name, level, hp }, spells) => ({
  namespaced: true,
  state: {
    name, level, hp, spells
  }
});
