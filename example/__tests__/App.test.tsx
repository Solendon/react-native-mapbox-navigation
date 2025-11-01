/**
 * @format
 */

import 'react-native';
import React from 'react';
import App from '../App';
import Demo from '../Demo';

// Note: import explicitly to use the types shipped with jest.
import { expect, it, test } from '@jest/globals';

// Note: test renderer must be required after react-native.
import { render } from '@testing-library/react-native';

it('renders correctly App', () => {
  render(<App />);
});

it('renders correctly Demo', () => {
  render(<Demo />);
});
