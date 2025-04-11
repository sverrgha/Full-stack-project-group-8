// cypress/e2e/login.cy.js
describe('Login Page', () => {
    beforeEach(() => {
        cy.visit('/login')
    })

    it('displays the login form correctly', () => {
        cy.contains('h1', 'Logg inn' || 'Login')
        cy.get('input[type="email"]').should('be.visible')
        cy.get('input[type="password"]').should('be.visible')
        cy.get('button[type="submit"]').should('be.visible')
    })

    it('validates empty form submission', () => {
        cy.get('button[type="submit"]').click()
        cy.get('input:invalid').should('have.length', 2)
    })

    it('validates email format', () => {
        cy.get('input[type="email"]').type('invalid-email')
        cy.get('button[type="submit"]').click()
        cy.get('input[type="email"]:invalid').should('have.length', 1)

        // Clear and type valid email format
        cy.get('input[type="email"]').clear().type('valid@example.com')
        cy.get('input[type="email"]:invalid').should('have.length', 0)
    })

    it('displays error message with invalid credentials', () => {
        // Mock failed login attempt
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 401,
            body: {
                error: 'Invalid credentials'
            }
        }).as('loginFailure')

        cy.get('input[type="email"]').type('wrong@example.com')
        cy.get('input[type="password"]').type('wrongpassword')
        cy.get('button[type="submit"]').click()
        cy.wait('@loginFailure')

        cy.get('.api-error').should('be.visible')
        cy.get('.api-error').contains('Logg inn feilet' || 'Login failed')
    })

    it('navigates to register page when clicking register link', () => {
        cy.contains('span', 'Registrer deg her' || 'Register here').click()
        cy.url().should('include', '/register')
    })

    it('successfully logs in with valid credentials', () => {
        // Mock successful login
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 200,
            body: {
                idToken: 'fake-token',
                email: 'test@example.com',
                displayName: 'Test User'
            }
        }).as('loginSuccess')

        cy.get('input[type="email"]').type('anders7020@gmail.com')
        cy.get('input[type="password"]').type('password')
        cy.get('button[type="submit"]').click()
        cy.wait('@loginSuccess')

        // Check if redirected to products page
        cy.url().should('include', '/products')
    })
})