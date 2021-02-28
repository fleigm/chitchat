module.exports = {
    purge: [],
    darkMode: false, // or 'media' or 'class'
    theme: {
        extend: {
            colors: {
                'primary': '#22292f',
                'secondary': '#606f7b',
                'tertiary': '#8795a1',

                'primary-inverse': '#ffffff',
                'secondary-inverse': '#f1f5f8',
                'tertiary-inverse': '#dae1e7',
            }
        },
        container: {
            center: true,
            padding: '2rem',
        },
    },
    variants: {
        extend: {},
    },
    plugins: [],
}
