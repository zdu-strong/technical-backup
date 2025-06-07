const NodePolyfillPlugin = require('node-polyfill-webpack-plugin');
const path = require('path');
const ModuleScopePlugin = require('react-dev-utils/ModuleScopePlugin');

module.exports = function override(config) {
  config.plugins = (config.plugins || []).concat([
    new NodePolyfillPlugin({
      additionalAliases: [
        "_stream_duplex",
        "_stream_passthrough",
        "_stream_readable",
        "_stream_transform",
        "_stream_writable",
        "console",
        "domain",
        "process",
        "punycode",
      ]
    })
  ]);
  config.resolve.alias['@'] = path.join(__dirname, 'src');
  config.resolve.alias['@component'] = path.join(__dirname, 'src/component');
  config.resolve.alias['@common'] = path.join(__dirname, 'src/common');
  config.resolve.alias['@api'] = path.join(__dirname, 'src/api');
  config.resolve.alias['@enums'] = path.join(__dirname, 'src/enums');
  config.resolve.alias['@model'] = path.join(__dirname, 'src/model');
  config.resolve.alias['@service'] = path.join(__dirname, 'src/service');
  config.resolve.plugins = config.resolve.plugins.filter(plugin => !(plugin instanceof ModuleScopePlugin));
  return config;
};