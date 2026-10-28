import 'react-native-url-polyfill/auto';
import { createClient } from '@supabase/supabase-js';

const supabaseUrl = 'https://xidsmsyzfcpmozliaazj.supabase.co';
const supabaseAnonKey = 'sb_publishable_ZMcBOu-4ONULqJu_n1ZGAw_fbg_91OV';

export const supabase = createClient(supabaseUrl, supabaseAnonKey);