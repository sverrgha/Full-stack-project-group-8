
import no from './locales/no.json'
import en from './locales/en.json'
import {createI18n} from "vue-i18n";

const messages = {
    no,
    en
};

const i18n = createI18n({
    legacy: false,
    locale: 'no',
    fallbackLocale: 'en',
    messages
});

export default i18n;