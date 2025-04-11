// cypress/e2e/register.cy.js
describe('Register Page', () => {
    beforeEach(() => {
        cy.visit('/register')
    })

    it('displays the registration form correctly', () => {
        cy.contains('h1', 'Registrer deg' || 'Register')
        cy.get('input#firstname').should('be.visible')
        cy.get('input#lastname').should('be.visible')
        cy.get('input#email').should('be.visible')
        cy.get('input#phone').should('be.visible')
        cy.get('input#password').should('be.visible')
        cy.get('input#confirmPassword').should('be.visible')
        cy.get('button[type="submit"]').should('be.visible')
    })

    it('validates empty form submission', () => {
        cy.get('button[type="submit"]').click()
        cy.contains('p.error-message', 'Fornavn er påkrevd' || 'Firstname is required')
        cy.contains('p.error-message', 'Etternavn er påkrevd' || 'Lastname is required')
        cy.contains('p.error-message', 'Skriv inn en gyldig e-post' || 'Enter a valid email')
        cy.contains('p.error-message', 'Skriv inn et gyldig telefonnummer' || 'Enter a valid phone number')
        cy.contains('p.error-message', 'Passord må være minst 8 tegn' || 'Password must be at least 8 characters')
    })

    it('validates phone number format', () => {
        cy.get('input#firstname').type('John')
        cy.get('input#lastname').type('Doe')
        cy.get('input#email').type('valid@example.com')
        cy.get('input#phone').type('abc123')
        cy.get('input#password').type('password123')
        cy.get('input#confirmPassword').type('password123')
        cy.get('button[type="submit"]').click()

        cy.contains('p.error-message', 'Skriv inn et gyldig telefonnummer' || 'Enter a valid phone number')
    })

    it('validates password length', () => {
        cy.get('input#firstname').type('John')
        cy.get('input#lastname').type('Doe')
        cy.get('input#email').type('valid@example.com')
        cy.get('input#phone').type('12345678')
        cy.get('input#password').type('short')
        cy.get('input#confirmPassword').type('short')
        cy.get('button[type="submit"]').click()

        cy.contains('p.error-message', 'Passord må være minst 8 tegn' || 'Password must be at least 8 characters')
    })

    it('validates password match', () => {
        cy.get('input#firstname').type('John')
        cy.get('input#lastname').type('Doe')
        cy.get('input#email').type('valid@example.com')
        cy.get('input#phone').type('12345678')
        cy.get('input#password').type('password123')
        cy.get('input#confirmPassword').type('password456')
        cy.get('button[type="submit"]').click()

        cy.contains('p.error-message', 'Passordene er ikke like' || 'Passwords do not match')
    })

    it('shows loading state during registration attempt', () => {
        cy.intercept('POST', '/api/auth/register', {
            delay: 500,
            statusCode: 200,
            body: {
                message: 'Registration successful'
            }
        }).as('registerRequest')

        cy.get('input#firstname').type('John')
        cy.get('input#lastname').type('Doe')
        cy.get('input#email').type('valid@example.com')
        cy.get('input#phone').type('12345678')
        cy.get('input#password').type('password123')
        cy.get('input#confirmPassword').type('password123')
        cy.get('button[type="submit"]').click()

        // Check loading state
        cy.get('button[type="submit"]').should('be.disabled')
        cy.get('button[type="submit"]').contains('registerForm.registering')

        cy.wait('@registerRequest')
    })

    it('displays error message with failed registration', () => {
        cy.intercept('POST', '/api/auth/register', {
            statusCode: 400,
            body: {
                message: 'Email already in use'
            }
        }).as('registerFailure')

        cy.get('input#firstname').type('John')
        cy.get('input#lastname').type('Doe')
        cy.get('input#email').type('existing@example.com')
        cy.get('input#phone').type('12345678')
        cy.get('input#password').type('password123')
        cy.get('input#confirmPassword').type('password123')
        cy.get('button[type="submit"]').click()
        cy.wait('@registerFailure')

        cy.get('.api-error').should('be.visible')
    })

    it('successfully registers with valid details', () => {
        cy.intercept('POST', '/api/auth/register', {
            statusCode: 200,
            body: {
                message: 'Registration successful'
            }
        }).as('registerSuccess')

        cy.get('input#firstname').type('John')
        cy.get('input#lastname').type('Doe')
        cy.get('input#email').type('new@example.com')
        cy.get('input#phone').type('12345678')
        cy.get('input#password').type('password123')
        cy.get('input#confirmPassword').type('password123')
        cy.get('button[type="submit"]').click()
        cy.wait('@registerSuccess')

        // Verify redirection after successful registration
        cy.url().should('include', '/login')
    })
})