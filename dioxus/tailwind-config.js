module.exports = {
  content: [
    "./src/**/*.{rs,html,css}",
    // Include Lumen Blocks components.
    // The `2675507` below is a shortened commit hash of the matching Lumen Blocks version.
    // When updating Lumen Blocks, please update this value as well.
    `${process.env.HOME}/.cargo/git/checkouts/lumen-blocks-*/2675507/blocks/src/**/*.rs`
  ],
  theme: {
    extend: {},
  },
  plugins: [],
};
