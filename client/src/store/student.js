export default ({ name, level, hp }) => ({
  namespaced: true,
  state: {
    name, level, hp
  }
});
